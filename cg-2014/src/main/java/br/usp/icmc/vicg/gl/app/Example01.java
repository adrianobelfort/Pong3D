package br.usp.icmc.vicg.gl.app;

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

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class Example01 implements GLEventListener {

  private final Shader shader; // Gerenciador dos shaders
  private int[] vbo; // Vertex Buffer Object
  private int nr_vertices;  // Numero de vertices no VBO

  public Example01() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.SIMPLE_SHADER);
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

    //cria o objeto a ser desenhado
    nr_vertices = create_object(gl);
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

    // Desenha o buffer carregado em memória (triangulos)
    gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nr_vertices);

    // Força execução das operações declaradas
    gl.glFlush();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Apaga o buffer
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    if (vbo[0] > 0) {
      gl.glDeleteBuffers(1, vbo, 0);
      vbo[0] = 0;
    }
  }

  private int create_object(GL3 gl) {
    // Triangle
    float[] TRIANGLE_VERTICES = new float[]{
      -0.5f, -0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      0.0f, 0.5f, 0.0f,};

    // create vertex positions buffer
    vbo = new int[1];
    gl.glGenBuffers(1, vbo, 0);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, TRIANGLE_VERTICES.length * Buffers.SIZEOF_FLOAT,
            Buffers.newDirectFloatBuffer(TRIANGLE_VERTICES), GL3.GL_STATIC_DRAW);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

    // pass buffer to shader program
    int vertexPositions = shader.getAttribLocation("a_position");
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
    gl.glVertexAttribPointer(vertexPositions, 3, GL3.GL_FLOAT, false, 0, 0);
    gl.glEnableVertexAttribArray(vertexPositions);

    return TRIANGLE_VERTICES.length / 3;
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
    glCanvas.addGLEventListener(new Example01());

    Frame frame = new Frame("Example 01");
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
