package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
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

import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import br.usp.icmc.vicg.gl.util.ShaderFactory.ShaderType;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RotationTest extends KeyAdapter implements GLEventListener {

  private final Shader shader; // Gerenciador dos shaders
  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final Matrix4 viewMatrix;
  private final JWavefrontObject model;
  private final Light light;
  private float alpha;
  private float beta;
  private float delta;
  private float gamma;

  public RotationTest() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
    modelMatrix = new Matrix4();
    projectionMatrix = new Matrix4();
    viewMatrix = new Matrix4();

    model = new JWavefrontObject(new File("./data/robot/robot.obj"));
    //model = new JWavefrontObject(new File("C:\\Users\\Adriano\\Dropbox\\USP\\2015.1 - Quinto semestre\\Computação Gráfica\\Projeto\\Objetos\\Tennis Court\\Tennis-Court.obj"));
    light = new Light();

    alpha = 0;
    beta = 0;
    delta = 5;
    gamma = 0;
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    // Get pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Print OpenGL version
    System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClearDepth(1.0f);

    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_CULL_FACE);

    //inicializa os shaders
    shader.init(gl);

    //ativa os shaders
    shader.bind();

    //inicializa a matrix Model and Projection
    modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
    projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
    viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));

    try {
      //init the model
      model.init(gl, shader);
      model.unitize();
      model.dump();
    } catch (IOException ex) {
      Logger.getLogger(Example09.class.getName()).log(Level.SEVERE, null, ex);
    }

    //init the light
    light.setPosition(new float[]{10, 10, 50, 1.0f});
    light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
    light.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
    light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
    light.init(gl, shader);
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

    projectionMatrix.loadIdentity();
    projectionMatrix.ortho(
            -delta, delta, 
            -delta, delta, 
            -2 * delta, 2 * delta);
    projectionMatrix.bind();

    modelMatrix.loadIdentity();
    //modelMatrix.scale(0.5f, 0.5f, 0.5f);
    modelMatrix.rotate(beta, 0, 1.0f, 0);
    modelMatrix.rotate(alpha, 1.0f, 0, 0);
    modelMatrix.rotate(gamma, 0, 0, 1.0f);
    modelMatrix.bind();

    viewMatrix.loadIdentity();
    viewMatrix.lookAt(
            1, 1, 1, 
            0, 0, 0, 
            0, 1, 0);
    viewMatrix.bind();

    light.bind();

    model.draw();

    // Força execução das operações declaradas
    gl.glFlush();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    model.dispose();
  }

  @Override
  public void keyPressed(KeyEvent e) {

    switch (e.getKeyCode()) {
        case KeyEvent.VK_PAGE_UP://faz zoom-in
            delta = delta * 0.809f;
            break;
        case KeyEvent.VK_PAGE_DOWN://faz zoom-out
            delta = delta * 1.1f;
            break;
        case KeyEvent.VK_UP://gira sobre o eixo-x
            alpha = alpha - 5;
            break;
        case KeyEvent.VK_DOWN://gira sobre o eixo-x
            alpha = alpha + 5;
            break;
        case KeyEvent.VK_LEFT://gira sobre o eixo-y
            beta = beta - 5;
            break;
        case KeyEvent.VK_RIGHT://gira sobre o eixo-y
            beta = beta + 5;
            break;
        case KeyEvent.VK_G:
            beta = beta + 5;
            break;
        case KeyEvent.VK_D:
            beta = beta - 5;
            break;
        case KeyEvent.VK_R:
            alpha = alpha - 5;
            break;
        case KeyEvent.VK_V:
            alpha = alpha + 5;
            break;
        case KeyEvent.VK_E:
            alpha = alpha - 5;
            beta = beta - 5;
            break;
        case KeyEvent.VK_C:
            alpha = alpha + 5;
            beta = beta - 5;
            break;
        case KeyEvent.VK_B:
            alpha = alpha + 5;
            beta = beta + 5;
            break;
        case KeyEvent.VK_T:
            alpha = alpha - 5;
            beta = beta + 5;
            break;
    }
  }

  public static void main(String[] args) {
    // Get GL3 profile (to work with OpenGL 4.0)
    GLProfile profile = GLProfile.get(GLProfile.GL3);

    // Configurations
    GLCapabilities glcaps = new GLCapabilities(profile);
    glcaps.setDoubleBuffered(true);
    glcaps.setHardwareAccelerated(true);

    // Create canvas
    GLCanvas glCanvas = new GLCanvas(glcaps);

    // Add listener to panel
    RotationTest listener = new RotationTest();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("Rotation Test");
    frame.setSize(600, 600);
    frame.add(glCanvas);
    frame.addKeyListener(listener);
    final AnimatorBase animator = new FPSAnimator(glCanvas, 60);

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            animator.stop();
            System.exit(0);
          }
        }).start();
      }
    });
    frame.setVisible(true);
    animator.start();
  }
}
