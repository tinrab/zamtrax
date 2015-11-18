#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec2 uv;
in layout(location = 2) vec3 normal;

out vec2 vUV;
out vec3 vNormal;
out vec4 vPosition;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat3 normalMatrix;

void main()
{
	vec4 mvPosition = modelViewMatrix * position;
	
	vUV = uv;
	vNormal = normalMatrix * normal;
	vPosition = mvPosition;

	gl_Position = projectionMatrix * mvPosition;
}
