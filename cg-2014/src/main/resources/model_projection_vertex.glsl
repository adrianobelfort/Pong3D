#version 150

uniform mat4 u_modelMatrix;
uniform mat4 u_projectionMatrix;

in vec3 a_position;

void main(void)
{  
  gl_Position = u_projectionMatrix * u_modelMatrix * vec4(a_position, 1.0);
}
