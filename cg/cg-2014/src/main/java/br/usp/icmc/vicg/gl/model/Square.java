/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import javax.media.opengl.GL;

/**
 *
 * @author PC
 */
public class Square extends SimpleModel {

  public Square() {
    vertex_buffer = new float[]{
      -0.5f, -0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      0.5f, 0.5f, 0.0f,
      -0.5f, 0.5f, 0.0f};

    normal_buffer = new float[]{
      0.0f, 0.0f, 1.0f,
      0.0f, 0.0f, 1.0f,
      0.0f, 0.0f, 1.0f,
      0.0f, 0.0f, 1.0f};
  }

  @Override
  public void draw() {
    draw(GL.GL_LINE_LOOP);
  }
}
