#version 330

#include "shaders/lights.fs"

uniform PointLight pointLight;

void main()
{
	vec4 light = calculatePointLight(pointLight, material, vNormal, vPosition);

	vDiffuseColor = texture(diffuse, vUV) * light;
}
