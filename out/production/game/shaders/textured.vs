#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec2 uv;
in layout(location = 2) vec3 normal;

out vec2 vertexUV;

uniform mat4 mvp;

void main()
{
  vertexUV = uv;

  gl_Position = mvp * position;
}
