#version 330

#include "shaders/lights.fs"

uniform SpotLight spotLight;

void main()
{
	vec4 light = calculateSpotLight(spotLight, material, vNormal, vPosition);
	vec4 cookieColor = vec4(1.0);
	float shadowFactor = 1.0;

    if(castShadows) {
        shadowFactor = calculateShadowFactor(shadowMap, vShadowMapCoords, shadowVarianceMin, lightBleed);
    }

	if(useCookie) {
		cookieColor = calculateCookie(cookie, vShadowMapCoords, cookieScale);
	}

	vDiffuseColor = texture(diffuse, vUV) * light * cookieColor * shadowFactor;
}
