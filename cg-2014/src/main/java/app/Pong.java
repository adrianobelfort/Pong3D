
package app;

import core.Light;
import core.Material;
import jwavefront.JWavefrontObject;
import matrix.Matrix4;
import model.SimpleModel;
import model.Parallelepiped;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import util.Shader;
import util.ShaderFactory;
import util.ShaderFactory.ShaderType;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import game.GameAgents;
import game.GameState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import multiplayer.GameClient;
import physics.*;

public class Pong extends KeyAdapter implements GLEventListener {

    private final Shader shader; // Gerenciador dos shaders
    private final Matrix4 modelMatrix;
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private final SimpleModel nearParallelepiped;
    private final SimpleModel farParallelepiped;
    private final SimpleModel leftParallelepiped;
    private final SimpleModel rightParallelepiped;
    private float nearParallelepipedDisplacement;
    private float farParallelepipedDisplacement;
    private float rotationParameterY;
    private float rotationParameterX;
    private float rotationParameterZ;
    
    private final float step;
    private final int timeDelay;
    public static Timer timer;
    public static Updater updater;
    
    private final BallModel ballModel;
    private final ParallelepipedModel leftParallelepipedModel;
    private final ParallelepipedModel rightParallelepipedModel;
    private final ParallelepipedModel nearParallelepipedModel;
    private final ParallelepipedModel farParallelepipedModel;
    
    private final Light light;
    private final Material material;
    private final JWavefrontObject ball;
    
    private float distanceFromCenter;
    private float parallelepipedLengthScale;
    private float zDistance;
    
    private float ballDisplacementX;
    private float ballDisplacementZ;
    
    private float cameraDistance;
    private float cameraHeight;
    
    private final float[] viewUpVector;
    
    public final GameAgents agents;
    public final GameState state;
    public AnimatorBase animator;
    public CollisionAnalyzer analyzer;
    public Thread multiplayerThread;
    public GameClient multiplayerHandler;
    
    private void printParameters()
    {
        System.out.println("Current distances:");
        System.out.println("\tFrom center: " + distanceFromCenter + " horizontal scalar factor: " + parallelepipedLengthScale);
        System.out.println("\tParallelepiped mutual z-distance: " + zDistance);
        System.out.println("Camera settings:");
        System.out.println("\tZ-distance from origin: " + cameraDistance + " camera height: " + cameraHeight);
        System.out.println("Positions:");
        System.out.println("\tUpper parallelepiped: " + nearParallelepipedDisplacement + " bottom parallelepiped: " + farParallelepipedDisplacement + "\n");
    }
    
    public Pong(String serverIP, AnimatorBase _animator) {
        // Carrega os shaders
        // Changed from VIEW_MODEL_PROJECTION_MATRIX_SHADER to COMPLETE_SHADER
        //shader = ShaderFactory.getInstance(ShaderType.VIEW_MODEL_PROJECTION_MATRIX_SHADER);
        shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
        modelMatrix = new Matrix4();
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();
        nearParallelepiped = new Parallelepiped();
        farParallelepiped = new Parallelepiped();
        leftParallelepiped = new Parallelepiped();
        rightParallelepiped = new Parallelepiped();
        
        step = 0.2f;
        timeDelay = 20;
        updater = new Updater();
        timer = new Timer(timeDelay, updater);
        
        nearParallelepipedDisplacement = 0.0f;
        farParallelepipedDisplacement = 0.0f;
        rotationParameterY = 0.0f;
        rotationParameterX = 0.0f;
        rotationParameterZ = 0.0f;
        distanceFromCenter = 10.0f;
        parallelepipedLengthScale = 11.0f;
        zDistance = 16.0f;
        
        ballDisplacementX = 0.0f;
        ballDisplacementZ = 0.0f;
        
        cameraDistance = 35.0f;
        cameraHeight = 31.0f;
        
        viewUpVector = new float[]{0.0f, 1.0f, 0.0f};
        
        light = new Light();
        material = new Material();
        ball = new JWavefrontObject(new File("./data/bola/bola.obj"));
        //ball = new JWavefrontObject(new File("./data/pokeball/Pokeball.obj"));
        
        analyzer = new CollisionAnalyzer();
        
        ballModel = new BallModel(-0.5f, 0.5f, -0.5f, 0.5f, 0.5f, (float) Math.random(), (float) Math.random());
        leftParallelepipedModel = new ParallelepipedModel(-0.5f - distanceFromCenter, 0.5f - distanceFromCenter, -1.5f * parallelepipedLengthScale, 1.5f * parallelepipedLengthScale);
        rightParallelepipedModel = new ParallelepipedModel(-0.5f + distanceFromCenter, 0.5f + distanceFromCenter, -1.5f * parallelepipedLengthScale, 1.5f * parallelepipedLengthScale);
        nearParallelepipedModel = new ParallelepipedModel(-1.5f, 1.5f, zDistance - 0.5f, zDistance + 0.5f);
        farParallelepipedModel = new ParallelepipedModel(-1.5f, 1.5f, -(zDistance + 0.5f), -(zDistance - 0.5f));
        
        animator = _animator;
        agents = new GameAgents(ballModel, nearParallelepipedModel, farParallelepipedModel, leftParallelepipedModel, rightParallelepipedModel);
        state = new GameState(animator, timer);
        multiplayerHandler = new GameClient(serverIP, agents, state);
        multiplayerThread = new Thread(multiplayerHandler);
        multiplayerThread.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Get pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Print OpenGL version
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

        gl.glClearColor(0.8f, 0.8f, 1, 0);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);

        //inicializa os shaders
        shader.init(gl);

        //ativa os shaders
        shader.bind();

        //inicializa a matrix Model and Projection
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
        viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));

        // Inicializa o sistema de coordenadas
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(45.0f, 1.0f, 0.1f, 100.0f); //50f before
        projectionMatrix.bind();
        
        viewMatrix.loadIdentity();
        viewMatrix.lookAt(0.0f, 5.0f, 20.0f, 
                        0.0f, 0.0f, 0.0f, 
                        viewUpVector[0], viewUpVector[1], viewUpVector[2]);
        viewMatrix.bind();
        
        try 
        {
            //init the model
            ball.init(gl, shader);
            ball.unitize();
            ball.dump();
        } catch (IOException ex) 
        {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        light.init(gl, shader);
        light.setPosition(new float[]{0.0f, 1.0f/*0.0f*/, 0.0f/*2.0f*/, 0.0f});
        light.setAmbientColor(new float[]{0.5f, 0.5f, 0.5f, 0.0f});
        light.setDiffuseColor(new float[]{0.5f, 0.5f, 0.5f, 0.0f});
        light.setSpecularColor(new float[]{0.9f, 0.9f, 0.9f, 0.0f});
        light.bind();

        material.init(gl, shader);
        material.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 0.0f});
        material.setDiffuseColor(new float[]{0.0f, 1.0f, 0.0f, 0.0f});
        material.setSpecularColor(new float[]{0.9f, 0.9f, 0.9f, 0.0f});
        material.setSpecularExponent(32);
        material.bind();

        //cria o objeto a ser desenhado
        nearParallelepiped.init(gl, shader);
        farParallelepiped.init(gl, shader);
        leftParallelepiped.init(gl, shader);
        rightParallelepiped.init(gl, shader);
    }

    @Override
    public void display(GLAutoDrawable drawable) 
    {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        // Here we will add the multi-camera features
        viewMatrix.loadIdentity();
        viewMatrix.lookAt(0.0f, cameraHeight, cameraDistance, 
                        0.0f, 0.0f, 0.0f, 
                        viewUpVector[0], viewUpVector[1], viewUpVector[2]);
        viewMatrix.bind();
        
        light.bind();
        material.bind();
        
        modelMatrix.loadIdentity();
        // Translates the object based on the pressing of the buttons
        modelMatrix.translate(nearParallelepipedDisplacement, 0, zDistance);
        modelMatrix.bind();

        nearParallelepiped.bind();
        nearParallelepiped.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.translate(farParallelepipedDisplacement, 0, -zDistance);
        modelMatrix.bind();
        
        farParallelepiped.bind();
        farParallelepiped.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(90, 0, 1, 0);
        modelMatrix.scale(parallelepipedLengthScale, 1, 1);
        modelMatrix.translate(0, 0, -distanceFromCenter);
        modelMatrix.bind();
        
        leftParallelepiped.bind();
        leftParallelepiped.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(90, 0, 1, 0);
        modelMatrix.scale(parallelepipedLengthScale, 1, 1);
        modelMatrix.translate(0, 0, distanceFromCenter);
        modelMatrix.bind();
        
        rightParallelepiped.bind();
        rightParallelepiped.draw();
        
        ballDisplacementX = ballModel.getX();
        ballDisplacementZ = ballModel.getZ();
        rotationParameterX = -1.0f * ballModel.rotationAngleAroundZ();
        rotationParameterZ = -1.0f * ballModel.rotationAngleAroundX();
        
        modelMatrix.loadIdentity();
        modelMatrix.translate(ballDisplacementX, 0, ballDisplacementZ);
        modelMatrix.rotate(rotationParameterY, 0, 1.0f, 0);
        modelMatrix.rotate(rotationParameterX, 0, 0, 1.0f);
        modelMatrix.rotate(rotationParameterZ, 1.0f, 0, 0);
        modelMatrix.scale(0.5f, 0.5f, 0.5f);
        modelMatrix.bind();  
        
        ball.draw();

        // Força execução das operações declaradas
        gl.glFlush();
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Apaga o buffer
        nearParallelepiped.dispose();
        farParallelepiped.dispose();
        ball.dispose();
    }
    
    public void bindAnimator(AnimatorBase a)
    {
        animator = a;
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        try
        {
            if(animator.isAnimating())
            {
                switch(e.getKeyCode())
                {
                    case KeyEvent.VK_LEFT: // Moves to the left
                        if (nearParallelepipedModel.move(-step, 0))
                        {
                            multiplayerHandler.sendBlockMove(-step, 0);
                        }
                    break;

                    case KeyEvent.VK_RIGHT: // Moves to the right

                        if (nearParallelepipedModel.move(step, 0))
                        {
                            multiplayerHandler.sendBlockMove(step, 0);
                        }
                    break;

                    case KeyEvent.VK_A:
                        farParallelepipedModel.move(-step, 0);
                    break;

                    case KeyEvent.VK_D:
                        farParallelepipedModel.move(step, 0);
                    break;

                    case KeyEvent.VK_NUMPAD4:
                        if (!ballModel.move(-step, 0))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD6:
                        if (!ballModel.move(step, 0))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD2:
                        if(!ballModel.move(0, step))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD8:
                        if (!ballModel.move(0, -step))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD1:
                        if(!ballModel.move(-step, step))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD3:
                        if(!ballModel.move(step, step))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD7:
                        if(!ballModel.move(-step, -step))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_NUMPAD9:
                        if(!ballModel.move(step, -step))
                        {
                            // handle collision + multiplayer
                        }
                    break;

                    case KeyEvent.VK_F: // Moves to the left
                        rotationParameterY -= step;
                    break;

                    case KeyEvent.VK_J: // Moves to the right
                        rotationParameterY += step;
                    break;

                    case KeyEvent.VK_Y:
                        distanceFromCenter += 2.0f * step;
                    break;

                    case KeyEvent.VK_H:
                        distanceFromCenter -= 2.0f * step;
                    break;

                    case KeyEvent.VK_T:
                        parallelepipedLengthScale += step;
                    break;

                    case KeyEvent.VK_G:
                        parallelepipedLengthScale -= step;
                    break;

                    case KeyEvent.VK_B:
                        zDistance -= step;
                    break;

                    case KeyEvent.VK_N:
                        zDistance += step;
                    break;

                    case KeyEvent.VK_X:
                        if (cameraDistance <= 2.0f * step)
                        {
                            cameraDistance = 0.1f;
                        }
                        else
                        {
                            cameraDistance -= 2.0f * step;
                        }
                    break;

                    case KeyEvent.VK_C:
                        if (cameraDistance < 2.0f * step)
                        {
                            cameraDistance = 2.0f * step;
                        }
                        else
                        {
                            cameraDistance += 2.0f * step;
                        }
                    break;

                    case KeyEvent.VK_V:
                        cameraHeight += 2.0f * step;
                    break;

                    case KeyEvent.VK_Z:
                        cameraHeight -= 2.0f * step;
                    break;

                    case KeyEvent.VK_U:
                        cameraHeight = 20.0f;
                        cameraDistance = 0.1f;
                    break;

                    case KeyEvent.VK_R:
                        ballModel.updateAbsolutePosition(0, 0);
                        ballModel.setSpeed((float) Math.random(), (float) Math.random());

                        // Multiplayer code
                        multiplayerHandler.sendGameStart(ballModel.getSpeeds());
                    break;
                }

                nearParallelepipedDisplacement = nearParallelepipedModel.getX();
                farParallelepipedDisplacement = farParallelepipedModel.getX();
            }

            // TODO: CONTINUE HERE!!! PAUSE GAME

            // Switch for pause game features -- forget it (for now hehe)
            // This switch will be evaluated all the time, regardless of animation
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_1:
                    if (!state.isPaused())
                    {
                        if(state.pauseGame(true))
                        {
                            multiplayerHandler.sendGamePause();
                        }
                    }
                    else 
                    {
                        if(state.resumeGame(true))
                        {
                            multiplayerHandler.sendGameResume();
                        }
                    }
                break;

                //  Remove this option later (?)
                case KeyEvent.VK_2:
                    if (timer.isRunning())
                    {
                        multiplayerHandler.sendTimerPause();
                        state.pauseTimer();
                    }
                    else
                    {
                        multiplayerHandler.sendTimerResume();
                        state.resumeTimer();
                    }
                break;
                    
                case KeyEvent.VK_S:
                    if (state.isBound() && multiplayerHandler.whoStarts())
                    {
                        System.out.println("Game should start now");
                        ballModel.updateAbsolutePosition(0, 0);
                        ballModel.setSpeed((float) Math.random(), (float) Math.random());
                        // Multiplayer code
                        multiplayerHandler.sendGameStart(ballModel.getSpeeds());
                        // End of multiplayer code
                        state.startGame();
                    }
                    else
                    {
                        if (!state.isBound())
                        {
                            System.out.println("You are not connected to another player");
                        }
                        if (!multiplayerHandler.whoStarts())
                        {
                            System.out.println(multiplayerHandler.getRivalNickname() + " is the one who starts the game");
                        }
                    }
                break;
                    
                case KeyEvent.VK_ESCAPE:
                    state.stopGame();
                    multiplayerHandler.disconnectFromPlayer();
                    multiplayerHandler.disconnectFromServer();
                    System.exit(1);
                break;
            }

            //printParameters();
        }
        catch(IOException ex)
        {
            System.out.println("IO Exception...");
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        // Get GL3 profile (to work with OpenGL 4.0)
        GLProfile profile = GLProfile.get(GLProfile.GL3);

        // Configurations
        GLCapabilities glcaps = new GLCapabilities(profile);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        // Create canvas
        GLCanvas glCanvas = new GLCanvas(glcaps);
        
        System.out.print("Enter the address of the game server: ");
        Scanner scanner = new Scanner(System.in);
        String serverIP = scanner.nextLine();
        
        // Add listener to panel
        final AnimatorBase animator = new FPSAnimator(glCanvas, 60);
        final Pong listener = new Pong(serverIP, animator);
        glCanvas.addGLEventListener(listener);

        Frame frame = new Frame("Pong 3D (beta)");
        frame.setSize(1020, 1020);
        frame.add(glCanvas);
        frame.addKeyListener(listener /*updater*/);
        //listener.bindAnimator(animator);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() 
                    {
                        listener.state.stopGame();
                        try 
                        {
                            listener.multiplayerHandler.disconnectFromPlayer();
                            listener.multiplayerHandler.disconnectFromServer();
                        } catch (IOException ex) {
                            //Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.exit(0);
                    }
                }).start();
            }

        });
        
        frame.setVisible(true);
        
        //animator.start();
        //timer.start();
        
        //listener.state.animatorThread.join();
        System.out.println("Program came to an end");
    }

    public class Updater implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {            
            ballModel.move(step);
        }   
    }
}
