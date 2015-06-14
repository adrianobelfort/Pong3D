/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.jwavefront;

/**
 *
 * @author Administrador
 */
public class VectorMath {

    public static float[] cross(float[] u, float[] v) {
        assert (u != null);
        assert (v != null);
        float[] n = new float[3];
        n[0] = u[1] * v[2] - u[2] * v[1];
        n[1] = u[2] * v[0] - u[0] * v[2];
        n[2] = u[0] * v[1] - u[1] * v[0];
        return n;
    }

    public static void normalize(float[] v) {
        float norm = (float) Math.sqrt(v[0] * v[0]
                + v[1] * v[1] + v[2] * v[2]);
        v[0] /= norm;
        v[1] /= norm;
        v[2] /= norm;
    }
}
