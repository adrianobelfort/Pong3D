/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.icmc.vicg.gl.jwavefront;

/**
 *
 * @author PC
 */
public class Triangle {

    public Vertex vertices[] = new Vertex[3]; // array of triangle vertex indices
    public Normal vertex_normals[] = new Normal[3]; // array of triangle normal indices
    public TextureCoord vertex_tex_coords[] = new TextureCoord[3]; // array of triangle texcoord indices
    public Normal face_normal = null;
}
