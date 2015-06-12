/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.icmc.vicg.gl.jwavefront;

/**
 *
 * @author PC
 */
public class Vertex {
    
    public Vertex(int id, float x, float y, float z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int id;
    public float x;
    public float y;
    public float z;
}
