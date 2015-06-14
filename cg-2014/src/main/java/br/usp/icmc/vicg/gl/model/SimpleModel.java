/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import br.usp.icmc.vicg.gl.util.Shader;
import com.jogamp.common.nio.Buffers;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

/**
 *
 * @author PC
 */
public abstract class SimpleModel {

  private GL3 gl;
  private int vertex_positions_handle;
  private int vertex_normals_handle;
  private int[] vao;
  protected float[] vertex_buffer;
  protected float[] normal_buffer;

  public abstract void draw();

  public void draw(int primitive) {
    // Desenha o buffer carregado em memória (triangulos)
    gl.glDrawArrays(primitive, 0, vertex_buffer.length / 3);
    gl.glBindVertexArray(0);
  }

  public void init(GL3 gl, Shader shader) {
    this.gl = gl;
    this.vertex_positions_handle = shader.getAttribLocation("a_position");
    this.vertex_normals_handle = shader.getAttribLocation("a_normal");
    create_object(gl);
  }

  public void bind() {
    gl.glBindVertexArray(vao[0]);
  }

  public void dispose() {
  }

  private void create_object(GL3 gl) {
    vao = new int[1];
    gl.glGenVertexArrays(1, vao, 0);
    gl.glBindVertexArray(vao[0]);

    // create vertex positions buffer
    int vbo[] = new int[2];
    gl.glGenBuffers(2, vbo, 0);

    //the positions buffer
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]); // Bind vertex buffer 
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertex_buffer.length * Buffers.SIZEOF_FLOAT,
            Buffers.newDirectFloatBuffer(vertex_buffer), GL3.GL_STATIC_DRAW);
    gl.glEnableVertexAttribArray(vertex_positions_handle);
    gl.glVertexAttribPointer(vertex_positions_handle, 3, GL3.GL_FLOAT, false, 0, 0);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

    if (normal_buffer != null) {
      //the normals buffer
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]); // Bind normals buffer
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, normal_buffer.length * Buffers.SIZEOF_FLOAT,
              Buffers.newDirectFloatBuffer(normal_buffer), GL3.GL_STATIC_DRAW);
      gl.glEnableVertexAttribArray(vertex_normals_handle);
      gl.glVertexAttribPointer(vertex_normals_handle, 3, GL3.GL_FLOAT, false, 0, 0);
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    }
  }
}
