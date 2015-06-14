package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.model.Triangle;
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

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class Example04 implements GLEventListener {

  private final Shader shader; // Gerenciador dos shaders
  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final SimpleModel triangle;

  public Example04() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.MODEL_PROJECTION_MATRIX_SHADER);
    modelMatrix = new Matrix4();
    projectionMatrix = new Matrix4();
    triangle = new Triangle();
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    // Get pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Print OpenGL version
    System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

    gl.glClearColor(0, 0, 0, 0);

    //inicializa os shaders
    shader.init(gl);

    //ativa os shaders
    shader.bind();

    //inicializa a matrix Model and Projection
    modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
    projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));

    //cria o objeto a ser desenhado
    triangle.init(gl, shader);

    //cria a projecao 2D
    projectionMatrix.loadIdentity();
    projectionMatrix.ortho2D(
            -0.501f, 0.501f, 
            -0.501f, 0.501f);
    projectionMatrix.bind();
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

    modelMatrix.loadIdentity();
    modelMatrix.bind();

    gl.glViewport(0, 0, 600, 575);
    triangle.bind();
    triangle.draw(GL3.GL_LINE_LOOP);

    gl.glViewport(0, 475, 100, 100);
    triangle.bind();
    triangle.draw(GL3.GL_LINE_LOOP);

    // Força execução das operações declaradas
    gl.glFlush();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    // Apaga o buffer
    triangle.dispose();
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
    Example04 listener = new Example04();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("Example 04");
    frame.setSize(600, 600);
    frame.add(glCanvas);
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
