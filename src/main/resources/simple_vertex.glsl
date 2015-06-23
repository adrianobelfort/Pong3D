#version 150

in vec3 a_position;

void main(void)
{
   gl_Position = vec4(a_position, 1.0);
}
