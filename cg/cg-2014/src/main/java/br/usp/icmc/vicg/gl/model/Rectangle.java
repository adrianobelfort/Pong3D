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
public class Rectangle extends SimpleModel {

  public Rectangle() {
    vertex_buffer = new float[]{
      -0.5f, -0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      0.5f, 0.5f, 0.0f,
      -0.5f, 0.5f, 0.0f};
  }

  @Override
  public void draw() {
    draw(GL.GL_LINE_LOOP);
  }
}
