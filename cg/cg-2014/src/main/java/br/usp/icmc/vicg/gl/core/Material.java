package br.usp.icmc.vicg.gl.core;

import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;
import com.jogamp.common.nio.Buffers;
import java.util.Arrays;

public class Material {

  private GL3 gl;
  private float[] ambientColor;
  private float[] diffuseColor;
  private float[] specularColor;
  private float specularExponent;
  private int ambientColorHandle;
  private int diffuseColorHandle;
  private int specularColorHandle;
  private int specularExponentHandle;

  public Material() {
    setAmbientColor(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
    setDiffuseColor(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
    setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    setSpecularExponent(0);
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

  public final void setSpecularExponent(float expoent) {
    this.specularExponent = expoent;
  }

  public void init(GL3 gl, Shader shader) {
    this.gl = gl;
    this.ambientColorHandle = shader.getUniformLocation("u_material.ambientColor");
    this.diffuseColorHandle = shader.getUniformLocation("u_material.diffuseColor");
    this.specularColorHandle = shader.getUniformLocation("u_material.specularColor");
    this.specularExponentHandle = shader.getUniformLocation("u_material.specularExponent");
  }

  public void bind() {
    gl.glUniform4fv(ambientColorHandle, 1, Buffers.newDirectFloatBuffer(ambientColor));
    gl.glUniform4fv(diffuseColorHandle, 1, Buffers.newDirectFloatBuffer(diffuseColor));
    gl.glUniform4fv(specularColorHandle, 1, Buffers.newDirectFloatBuffer(specularColor));
    gl.glUniform1f(specularExponentHandle, specularExponent);
  }
}
