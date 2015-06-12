
package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.model.Sphere;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Example03 extends KeyAdapter implements GLEventListener {

    private final Shader shader; // Gerenciador dos shaders
    private final Matrix4 modelMatrix;
    private final SimpleModel triangle;

    private float tx = 0;
    private float ty = 0;
    private float delta = 1;

    public Example03() {
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.MODEL_MATRIX_SHADER);
        modelMatrix = new Matrix4();
        triangle = new Sphere();
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

        //inicializa a matrix Model
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));

        //cria o objeto a ser desenhado
        triangle.init(gl, shader);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

        modelMatrix.loadIdentity();
        modelMatrix.translate(tx, ty, 0);
        modelMatrix.scale(delta, delta, 1);
        modelMatrix.bind();

        // Desenha o buffer carregado em memória (triangulos)
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

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_PAGE_UP:
                delta *= 1.1f;
                break;
            case KeyEvent.VK_PAGE_DOWN:
                delta *= 0.809f;
                break;
            case KeyEvent.VK_UP:
                ty = ty + 0.1f;
                break;
            case KeyEvent.VK_DOWN:
                ty = ty - 0.1f;
                break;
            case KeyEvent.VK_LEFT:
                tx = tx - 0.1f;
                break;
            case KeyEvent.VK_RIGHT:
                tx = tx + 0.1f;
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
        Example03 listener = new Example03();
        glCanvas.addGLEventListener(listener);

        Frame frame = new Frame("Example 03");
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
