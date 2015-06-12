/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.jwavefront;

import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class Group {

    public static Group default_group = new Group("_default_");

    public Group(String name) {
        this.name = name;
        this.material = Material.default_material;
        this.triangles = new ArrayList<Triangle>();
    }

    /**
     * Generates facet normals for a model (by taking the cross product of the
     * two vectors derived from the sides of each triangle). Assumes a
     * counter-clockwise winding.
     */
    public void calculate_face_normals() {
        float u[] = new float[3];
        float v[] = new float[3];

        for (int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);

            u[0] = triangle.vertices[1].x - triangle.vertices[0].x;
            u[1] = triangle.vertices[1].y - triangle.vertices[0].y;
            u[2] = triangle.vertices[1].z - triangle.vertices[0].z;

            v[0] = triangle.vertices[2].x - triangle.vertices[0].x;
            v[1] = triangle.vertices[2].y - triangle.vertices[0].y;
            v[2] = triangle.vertices[2].z - triangle.vertices[0].z;

            float[] n = VectorMath.cross(u, v);
            VectorMath.normalize(n);

            triangle.face_normal = new Normal(n[0], n[1], n[2]);
        }
    }

    public void dump() {
        System.out.println("Group name: " + name);
        System.out.println("Number triangles: " + triangles.size());
        material.dump();
   
    }
    public String name; // name of this group
    public ArrayList<Triangle> triangles; // array of triangle indices
    public Material material; // index to material for group
}
