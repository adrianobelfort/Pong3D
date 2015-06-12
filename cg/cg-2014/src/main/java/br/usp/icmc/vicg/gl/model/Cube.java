/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import javax.media.opengl.GL3;

/**
 *
 * @author PC
 */
public class Cube extends SimpleModel {

  public Cube() {
    normal_buffer = new float[]{
      //front face
      0, 0, 1,
      0, 0, 1,
      0, 0, 1,
      0, 0, 1,
      0, 0, 1,
      0, 0, 1,
      //right face
      1, 0, 0,
      1, 0, 0,
      1, 0, 0,
      1, 0, 0,
      1, 0, 0,
      1, 0, 0,
      //back face
      0, 0, -1,
      0, 0, -1,
      0, 0, -1,
      0, 0, -1,
      0, 0, -1,
      0, 0, -1,
      //left face
      -1, 0, 0,
      -1, 0, 0,
      -1, 0, 0,
      -1, 0, 0,
      -1, 0, 0,
      -1, 0, 0,
      //bottom face
      0, -1, 0,
      0, -1, 0,
      0, -1, 0,
      0, -1, 0,
      0, -1, 0,
      0, -1, 0,
      //top face
      0, 1, 0,
      0, 1, 0,
      0, 1, 0,
      0, 1, 0,
      0, 1, 0,
      0, 1, 0};


    vertex_buffer = new float[]{
      // Front face
      -0.5f, -0.5f, 0.5f,
      0.5f, -0.5f, 0.5f,
      0.5f, 0.5f, 0.5f,
      0.5f, 0.5f, 0.5f,
      -0.5f, 0.5f, 0.5f,
      -0.5f, -0.5f, 0.5f,
      // Right face
      0.5f, -0.5f, 0.5f,
      0.5f, -0.5f, -0.5f,
      0.5f, 0.5f, -0.5f,
      0.5f, 0.5f, -0.5f,
      0.5f, 0.5f, 0.5f,
      0.5f, -0.5f, 0.5f,
      // Back face
      0.5f, -0.5f, -0.5f,
      -0.5f, -0.5f, -0.5f,
      -0.5f, 0.5f, -0.5f,
      -0.5f, 0.5f, -0.5f,
      0.5f, 0.5f, -0.5f,
      0.5f, -0.5f, -0.5f,
      // Left face
      -0.5f, -0.5f, 0.5f,
      -0.5f, 0.5f, 0.5f,
      -0.5f, 0.5f, -0.5f,
      -0.5f, 0.5f, -0.5f,
      -0.5f, -0.5f, -0.5f,
      -0.5f, -0.5f, 0.5f,
      // Bottom face
      -0.5f, -0.5f, 0.5f,
      -0.5f, -0.5f, -0.5f,
      0.5f, -0.5f, -0.5f,
      0.5f, -0.5f, -0.5f,
      0.5f, -0.5f, 0.5f,
      -0.5f, -0.5f, 0.5f,
      // Top face
      -0.5f, 0.5f, 0.5f,
      0.5f, 0.5f, 0.5f,
      0.5f, 0.5f, -0.5f,
      0.5f, 0.5f, -0.5f,
      -0.5f, 0.5f, -0.5f,
      -0.5f, 0.5f, 0.5f};
  }

  @Override
  public void draw() {
    draw(GL3.GL_TRIANGLES);
  }
}
