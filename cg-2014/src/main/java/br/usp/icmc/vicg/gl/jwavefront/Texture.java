/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.icmc.vicg.gl.jwavefront;

/**
 *
 * @author PC
 */
public class Texture {

    public Texture(String name) {
        this.name = name;
    }

    public void dump() {
        System.out.println("Texture name: " + name);
    }

    public String name;
    public com.jogamp.opengl.util.texture.Texture texturedata; //texture
}
