#version 150

in vec3 a_position;

void main(void)
{
  //criando uma matriz de escala 2D
  mat3 model = mat3(0.5, 0.0, 0.0,  //primeira coluna
                    0.0, 0.5, 0.0,  //segunda coluna
                    0.0, 0.0, 1.0); //terceira coluna

  //multiplicando a matriz de transformacao pelo vetor
  //em coordenadas homogeneas
  vec3 pos = model * vec3(a_position[0], a_position[1], 1.0);

  //convertendo as coordenadas homogeneas para euclideanas
  gl_Position = vec4(pos[0]/pos[2], pos[1]/pos[2], 0.0, 1.0);
}
