/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import javax.media.opengl.GL;

/**
 *
 * @author paulovich
 */
public class Circle extends SimpleModel {

  private final int nr_vertices = 50;

  public Circle() {
    vertex_buffer = new float[nr_vertices * 3];

    for (int i = 0, k = 0; i < nr_vertices; i++, k++) {
      float x = (float) Math.cos(Math.toRadians((360.0f * i) / nr_vertices));
      float y = (float) Math.sin(Math.toRadians((360.0f * i) / nr_vertices));
      float z = 0;

      vertex_buffer[k] = x;
      vertex_buffer[++k] = y;
      vertex_buffer[++k] = z;
    }
  }

  @Override
  public void draw() {
    draw(GL.GL_LINE_LOOP);
  }
}
