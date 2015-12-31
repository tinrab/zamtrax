#version 330

#include "shaders/lights.fs"

uniform SpotLight spotLight;

void main()
{
	vec4 light = calculateSpotLight(spotLight, material, vNormal, vPosition);
	float shadowFactor = calculateShadowFactor(shadowMap, vShadowMapCoords, shadowVarianceMin, lightBleed);

	vDiffuseColor = texture(diffuse, vUV) * light * shadowFactor;
}
