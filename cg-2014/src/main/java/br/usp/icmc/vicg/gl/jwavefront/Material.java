/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.icmc.vicg.gl.jwavefront;

/**
 *
 * @author PC
 */
public class Material {
    
    public static Material default_material = new Material("_default_");
    
    public Material(String name) {
        this.name = name;
        this.shininess = 65.0f;
        this.diffuse = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
        this.ambient = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
        this.specular = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    }

    public void dump() {
        System.out.println("Material name: " + name);
        System.out.println("Kd: (" + diffuse[0] + "," + diffuse[1] + "," + diffuse[2] + "," + diffuse[3] + ")");
        System.out.println("Ks: (" + specular[0] + "," + specular[1] + "," + specular[2] + "," + specular[3] + ")");
        System.out.println("Ka: (" + ambient[0] + "," + ambient[1] + "," + ambient[2] + "," + ambient[3] + ")");
        System.out.println("Ns: " + shininess);

        if(texture != null) {
            texture.dump();
        }
    }

    public String name; // name of material
    public float diffuse[]; // diffuse component
    public float ambient[]; // ambient component
    public float specular[]; // specular component
    public float shininess; // specular exponent
    public Texture texture; //texture
}
