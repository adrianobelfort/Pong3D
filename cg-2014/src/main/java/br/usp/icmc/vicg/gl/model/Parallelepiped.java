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
public class Parallelepiped extends SimpleModel {

    public Parallelepiped() {
        // Added normal buffer
        normal_buffer = new float[]{
            //front face
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            //0, 0, 1,
            //0, 0, 1,
            //right face
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            //1, 0, 0,
            //1, 0, 0,
            //back face
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            //0, 0, -1,
            //0, 0, -1,
            //left face
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            //-1, 0, 0,
            //-1, 0, 0,
            //bottom face
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            //0, -1, 0,
            //0, -1, 0,
            //top face
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            //0, 1, 0,
            //0, 1, 0,
            0, 1, 0,
            0, 1, 0};
        
        vertex_buffer = new float[]{
            // Front face
            -1.5f, -0.5f, 0.5f,
            1.5f, -0.5f, 0.5f,
            1.5f, 0.5f, 0.5f,
            1.5f, 0.5f, 0.5f,
            -1.5f, 0.5f, 0.5f,
            -1.5f, -0.5f, 0.5f,
            // Right face
            1.5f, -0.5f, 0.5f,
            1.5f, -0.5f, -0.5f,
            1.5f, 0.5f, -0.5f,
            1.5f, 0.5f, -0.5f,
            1.5f, 0.5f, 0.5f,
            1.5f, -0.5f, 0.5f,
            // Back face
            1.5f, -0.5f, -0.5f,
            -1.5f, -0.5f, -0.5f,
            -1.5f, 0.5f, -0.5f,
            -1.5f, 0.5f, -0.5f,
            1.5f, 0.5f, -0.5f,
            1.5f, -0.5f, -0.5f,
            // Left face
            -1.5f, -0.5f, 0.5f,
            -1.5f, 0.5f, 0.5f,
            -1.5f, 0.5f, -0.5f,
            -1.5f, 0.5f, -0.5f,
            -1.5f, -0.5f, -0.5f,
            -1.5f, -0.5f, 0.5f,
            // Bottom face
            -1.5f, -0.5f, 0.5f,
            -1.5f, -0.5f, -0.5f,
            1.5f, -0.5f, -0.5f,
            1.5f, -0.5f, -0.5f,
            1.5f, -0.5f, 0.5f,
            -1.5f, -0.5f, 0.5f,
            // Top face
            -1.5f, 0.5f, 0.5f,
            1.5f, 0.5f, 0.5f,
            1.5f, 0.5f, -0.5f,
            1.5f, 0.5f, -0.5f,
            -1.5f, 0.5f, -0.5f,
            -1.5f, 0.5f, 0.5f};
//            // Front face
//           -1.5f, -0.5f, 0.5f, 
//            1.5f, -0.5f, 0.5f, 
//            1.5f, -0.5f, 0.5f, 
//            1.5f, 0.5f, 0.5f, 
//            1.5f, 0.5f, 0.5f, 
//            -1.5f, 0.5f, 0.5f,
//            -1.5f, 0.5f, 0.5f,
//            -1.5f, -0.5f, 0.5f,
//          
//            // Right face
//            1.5f, -0.5f, 0.5f, 
//            1.5f, -0.5f, -0.5f,
//            1.5f, -0.5f, -0.5f,
//            1.5f, 0.5f, -0.5f,
//            1.5f, 0.5f, -0.5f,
//            1.5f, 0.5f, 0.5f,
//            1.5f, 0.5f, 0.5f,
//            1.5f, -0.5f, 0.5f, 
//            
//            // Back face
//            1.5f, -0.5f, -0.5f,            
//            -1.5f, -0.5f, -0.5f,
//            -1.5f, -0.5f, -0.5f,
//            -1.5f, 0.5f, -0.5f,
//            -1.5f, 0.5f, -0.5f,
//            1.5f, 0.5f, -0.5f,
//            1.5f, 0.5f, -0.5f,            
//            1.5f, -0.5f, -0.5f, 
//            
//            // Left face
//            -1.5f, -0.5f, 0.5f,
//            -1.5f, 0.5f, 0.5f,
//            -1.5f, 0.5f, 0.5f,
//            -1.5f, 0.5f, -0.5f,
//            -1.5f, 0.5f, -0.5f,
//            -1.5f, -0.5f, -0.5f,
//            -1.5f, -0.5f, -0.5f,
//            -1.5f, -0.5f, 0.5f,
//
//            // Bottom face
//            -1.5f, -0.5f, 0.5f,
//            -1.5f, -0.5f, -0.5f,
//            -1.5f, -0.5f, -0.5f,
//            1.5f, -0.5f, -0.5f,
//            1.5f, -0.5f, -0.5f,
//            1.5f, -0.5f, 0.5f,            
//            1.5f, -0.5f, 0.5f,
//            -1.5f, -0.5f, 0.5f,
//            
//            // Top face
//            -1.5f, 0.5f, 0.5f,
//            1.5f, 0.5f, 0.5f,
//            1.5f, 0.5f, 0.5f,
//            1.5f, 0.5f, -0.5f,
//            1.5f, 0.5f, -0.5f,
//            -1.5f, 0.5f, -0.5f,
//            -1.5f, 0.5f, -0.5f,
//            -1.5f, 0.5f, 0.5f,
        
    }

    @Override
    public void draw() {
        //draw(GL3.GL_LINES);
        draw(GL3.GL_TRIANGLES);
    }

}
