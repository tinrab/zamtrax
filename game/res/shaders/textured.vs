#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec2 uv;
in layout(location = 2) vec3 normal;

out vec2 vertexUV;
out vec3 vertexNormal;
out vec4 vertexPosition;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
//uniform mat3 normalMatrix;

void main()
{
	mat3 normalMatrix  = mat3(transpose(inverse(modelViewMatrix)));
	
	vertexUV = uv;
	vertexNormal = normalMatrix * normal;
	vertexPosition = modelViewMatrix * position;

	gl_Position = projectionMatrix * modelViewMatrix * position;
}
