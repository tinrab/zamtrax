#version 330

#include "shaders/lights.fs"

uniform PointLight pointLight;

void main()
{
	vec4 light = calculatePointLight(pointLight, material, vNormal, vPosition);
	float shadowFactor = calculateShadowFactor(shadowMap, vShadowMapCoords, shadowVarianceMin, lightBleed);

	vDiffuseColor = texture(diffuse, vUV) * light * shadowFactor;
}
