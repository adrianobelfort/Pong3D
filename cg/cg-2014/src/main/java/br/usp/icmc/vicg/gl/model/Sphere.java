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
public class Sphere extends SimpleModel {

    private final int lats;
    private final int longs;
    private final float radius;

    public Sphere() {
        this.radius = 1;
        this.lats = 40;
        this.longs = 40;

        vertex_buffer = new float[(lats + 1) * (longs + 1) * 6];

        int i, j, k;
        for (i = 0, k = -1; i <= lats; i++) {
            float lat0 = (float) (Math.PI * (-0.5f + (float) (i - 1) / lats));
            float z0 = (float) Math.sin(lat0);
            float zr0 = (float) Math.cos(lat0);

            float lat1 = (float) (Math.PI * (-0.5f + (float) i / lats));
            float z1 = (float) Math.sin(lat1);
            float zr1 = (float) Math.cos(lat1);

            for (j = 0; j <= longs; j++) {
                float lng = (float) (2 * Math.PI * (float) (j - 1) / longs);
                float x = (float) Math.cos(lng);
                float y = (float) Math.sin(lng);
                
                vertex_buffer[++k] = x * zr0 * radius;
                vertex_buffer[++k] = y * zr0 * radius;
                vertex_buffer[++k] = z0 * radius;
                
                vertex_buffer[++k] = x * zr1 * radius;
                vertex_buffer[++k] = y * zr1 * radius;
                vertex_buffer[++k] = z1 * radius;
            }
        }
    }

    @Override
    public void draw() {
        draw(GL.GL_LINE_LOOP);
    }
}
