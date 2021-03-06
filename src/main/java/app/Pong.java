/*  UNIVERSIDADE DE SAO PAULO
    INSTITUTO DE CIENCIAS MATEMATICAS E DE COMPUTACAO

    SCC 0650 - Computacao Grafica (2015) | Engenharia de Computacao
    Projeto: pongl - uma experiencia 3D

    Professora: Maria Cristina Ferreira de Oliveira
    Alunos:
        Adriano Belfort de Sousa 7960706
        Rodrigo Almeida Bergamo Ferrari 8006421
        Ilan Galvao Sales Figueiredo 7656321
*/


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

public class Pong extends KeyAdapter implements GLEventListener, Runnable 
{
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
    
    public static boolean collisionWithWall;
    
    private final BallModel ballModel;
    private final ParallelepipedModel nearParallelepipedModel;
    private final ParallelepipedModel farParallelepipedModel;
    
    private final Light light;
    private final Material material;
    private final JWavefrontObject ball;
    
    private float ballDisplacementX;
    private float ballDisplacementZ;
    
    private float cameraDistance;
    private float cameraHeight;
    
    private final float[] viewUpVector;
    
    public GameAgents agents;
    public GameState state;
    public AnimatorBase animator;
//    public CollisionAnalyzer analyzer;
    public GameClient multiplayerHandler;
    
    public GLCanvas glCanvas;
    private static Pong painter;
    private Frame frame;
    
    public static void main(String[] args) throws InterruptedException
    {
        // Will be moved to the interface
        System.out.print("Enter the address of the game server: ");
        Scanner scanner = new Scanner(System.in);
        String serverIP = scanner.nextLine();
        
        GameAgents newAgents = new GameAgents();
        GameState newState = new GameState();
        GameClient newMultiplayerHandler = new GameClient(serverIP, newAgents, newState);
        Thread multiplayerThread = new Thread(newMultiplayerHandler);
        multiplayerThread.start();
        
        painter = new Pong(newAgents, newState, newMultiplayerHandler);
        
        new Thread(painter).start();
    }
    
    @Override
    public void run() 
    {
    }
    
    public Pong(GameAgents agents, final GameState state, GameClient multiplayerClientHandler) 
    {
        // Carrega os shaders
        // Changed from VIEW_MODEL_PROJECTION_MATRIX_SHADER to COMPLETE_SHADER
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
        
        ballDisplacementX = 0.0f;
        ballDisplacementZ = 0.0f;
        
        cameraDistance = 35.0f;
        cameraHeight = 31.0f;
        
        viewUpVector = new float[]{0.0f, 1.0f, 0.0f};
        
        light = new Light();
        material = new Material();
        ball = new JWavefrontObject(new File("./data/bola/bola.obj"));
        
        this.agents = agents;
        ballModel = agents.getBall();
        nearParallelepipedModel = agents.getPlayerBlock();
        farParallelepipedModel = agents.getRivalBlock();
        
        this.state = state;
        this.multiplayerHandler = multiplayerClientHandler;
        
        // Get GL3 profile (to work with OpenGL 4.0)
        GLProfile profile = GLProfile.get(GLProfile.GL3);

        // Configurations
        GLCapabilities glcaps = new GLCapabilities(profile);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        // Create canvas -- gui does that!
        glCanvas = new GLCanvas(glcaps);
        
        // Add listener to panel
        animator = new FPSAnimator(glCanvas, 60);
        //final Pong listener = new Pong(serverIP, animator);
        glCanvas.addGLEventListener(this);
        
        state.bindPainter(this);
        
        frame = new Frame("pongl: a 3D experience");
        frame.setSize(800, 800);
        frame.add(glCanvas);
        frame.addKeyListener(this);

        frame.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                new Thread(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        state.stopGame();
                        try 
                        {
                            if (multiplayerHandler != null) 
                            {
                                multiplayerHandler.disconnectFromPlayer();
                                multiplayerHandler.disconnectFromServer();
                            }
                        } 
                        catch (IOException ex) 
                        {
                            //Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.exit(0);
                    }
                }).start();
            }

        });
        
        frame.setVisible(true);
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
        light.setPosition(new float[]{0.0f, 1.0f, 0.0f, 0.0f});
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
        modelMatrix.translate(nearParallelepipedDisplacement, 0, agents.getZDistance());
        modelMatrix.bind();

        nearParallelepiped.bind();
        nearParallelepiped.draw();
        
        farParallelepipedDisplacement = farParallelepipedModel.getX();
        
        modelMatrix.loadIdentity();
        modelMatrix.translate(farParallelepipedDisplacement, 0, -agents.getZDistance());
        modelMatrix.bind();
        
        farParallelepiped.bind();
        farParallelepiped.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(90, 0, 1, 0);
        modelMatrix.scale(agents.getParallelepipedLengthScale(), 1, 1);
        modelMatrix.translate(0, 0, -agents.getXDistance());
        modelMatrix.bind();
        
        leftParallelepiped.bind();
        leftParallelepiped.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(90, 0, 1, 0);
        modelMatrix.scale(agents.getParallelepipedLengthScale(), 1, 1);
        modelMatrix.translate(0, 0, agents.getXDistance());
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
                        float[] speed = ballModel.getSpeeds();
                        float result = 0.75f * (float) Math.sqrt(Math.pow((double)speed[0], 2.0) + Math.pow((double)speed[1], 2.0));
                
                        if (nearParallelepipedModel.move(-result, 0))
                        {
                            multiplayerHandler.sendBlockMove(-result, 0);
                        }
                    break;

                    case KeyEvent.VK_RIGHT: // Moves to the right
                        float[] speed2 = ballModel.getSpeeds();
                        float result2 = 0.75f * (float) Math.sqrt(Math.pow((double)speed2[0], 2.0) + Math.pow((double)speed2[1], 2.0));
                
                        if (nearParallelepipedModel.move(result2, 0))
                        {
                            multiplayerHandler.sendBlockMove(result2, 0);
                        }
                    break;

                    case KeyEvent.VK_F: // Moves to the left
                        rotationParameterY -= step;
                    break;

                    case KeyEvent.VK_J: // Moves to the right
                        rotationParameterY += step;
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
                        ballModel.setRandomSpeed();

                        // Multiplayer code
                        multiplayerHandler.sendGameStart(ballModel.getSpeeds());
                    break;
                }

                nearParallelepipedDisplacement = nearParallelepipedModel.getX();
            }

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
                        System.out.println("Game should start now");
                        ballModel.updateAbsolutePosition(0, 0);
                        ballModel.setRandomSpeed();
                        // Multiplayer code
                        multiplayerHandler.sendGameStart(ballModel.getSpeeds());
                        
                break;
                    
                case KeyEvent.VK_ESCAPE:
                    state.stopGame();
                    multiplayerHandler.disconnectFromPlayer();
                    multiplayerHandler.disconnectFromServer();
                    System.exit(1);
                break;
            }

        }
        catch(IOException ex)
        {
            System.out.println("IO Exception...");
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AnimatorBase getAnimator()
    {
        return animator;
    }
    
    public Timer getTimer()
    {
        return timer;
    }

    public class Updater implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {       
            float[] speed3 = ballModel.getSpeeds();
            // se o resultado for verdadeiro, houve colisao com os planos controlados pelo usuarios
            if(ballModel.move(step))
            {
                try {
                    multiplayerHandler.sendCollision(ballModel.getX(), ballModel.getZ(), speed3[0], speed3[1]);
                } catch (IOException ex) {
                    Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }   
    }
}
