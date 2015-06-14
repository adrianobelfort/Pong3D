package br.usp.icmc.vicg.gl.core;

import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;

import com.jogamp.common.nio.Buffers;
import java.util.Arrays;

public class Light {

  private GL3 gl;
  private float[] ambientColor;
  private float[] diffuseColor;
  private float[] specularColor;
  private float[] position;
  private int positionHandle;
  private int ambientColorHandle;
  private int diffuseColorHandle;
  private int specularColorHandle;

  public Light() {
    setPosition(new float[]{0.0f, 0.0f, 1.0f, 0.0f});
    setAmbientColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
    setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
  }

  public final void setPosition(float[] position) {
    this.position = Arrays.copyOf(position, position.length);
  }

  public final void setAmbientColor(float[] ambientColor) {
    this.ambientColor = Arrays.copyOf(ambientColor, ambientColor.length);
  }

  public final void setDiffuseColor(float[] diffuseColor) {
    this.diffuseColor = Arrays.copyOf(diffuseColor, diffuseColor.length);
  }

  public final void setSpecularColor(float[] specularColor) {
    this.specularColor = Arrays.copyOf(specularColor, specularColor.length);
  }

  public void init(GL3 gl, Shader shader) {
    this.gl = gl;
    this.positionHandle = shader.getUniformLocation("u_light.position");
    this.ambientColorHandle = shader.getUniformLocation("u_light.ambientColor");
    this.diffuseColorHandle = shader.getUniformLocation("u_light.diffuseColor");
    this.specularColorHandle = shader.getUniformLocation("u_light.specularColor");
  }

  public void bind() {
    gl.glUniform4fv(positionHandle, 1, Buffers.newDirectFloatBuffer(position));
    gl.glUniform4fv(ambientColorHandle, 1, Buffers.newDirectFloatBuffer(ambientColor));
    gl.glUniform4fv(diffuseColorHandle, 1, Buffers.newDirectFloatBuffer(diffuseColor));
    gl.glUniform4fv(specularColorHandle, 1, Buffers.newDirectFloatBuffer(specularColor));
  }
}
