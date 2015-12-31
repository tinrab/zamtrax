#version 330

#include "shaders/lights.fs"

uniform DirectionalLight directionalLight;

void main()
{
	vec4 light = calculateDirectionalLight(directionalLight, material, vNormal, vPosition);
	float shadowFactor = calculateShadowFactor(shadowMap, vShadowMapCoords, shadowVarianceMin, lightBleed);

	vDiffuseColor = texture(diffuse, vUV) * light * shadowFactor;
}