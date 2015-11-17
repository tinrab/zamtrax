#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec2 uv;
in layout(location = 2) vec3 normal;

out vec2 vertexUV;
out vec3 lightWeighting;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
//uniform mat3 normalMatrix;

uniform vec3 ambientColor;
uniform vec3 lightingDirection;
uniform vec3 directionalColor;

void main()
{
  vertexUV = uv;

  mat3 normalMatrix  = mat3(transpose(inverse(modelViewMatrix)));
  vec3 transformedNormal = normalMatrix * normal;
  float directionalLightWeighting = max(dot(transformedNormal, lightingDirection), 0.0);
  lightWeighting = ambientColor * directionalColor * directionalLightWeighting;

  gl_Position = projectionMatrix * modelViewMatrix * position;
}
