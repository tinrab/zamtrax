struct Light
{
	vec4 color;
	float intensity;
};

struct DirectionalLight
{
	Light base;
	vec3 direction;
};

struct PointLight
{
	Light base;
	vec3 position;
	float range;
};

struct SpotLight
{
	PointLight base;
	vec3 direction;
	float cutoff;
};

struct Material
{
    float shininess;
    float specularIntensity;
};

vec4 calculateLight(Light light, Material material, vec3 lightDirection, vec3 normal, vec4 position)
{
	float diffuseFactor = dot(normal, -lightDirection);

	vec4 diffuseColor = vec4(0.0);
	vec4 specularColor = vec4(0.0);

	diffuseColor = light.color * light.intensity * diffuseFactor;

	vec3 vertexToEye = normalize(-position.xyz);
	vec3 reflectionDirection = reflect(-lightDirection, normal);
	float specularFactor = max(0.0, dot(vertexToEye, reflectionDirection));

	specularFactor = pow(specularFactor, material.shininess);
	specularColor = light.color * specularFactor * material.specularIntensity;

	return diffuseColor;
}

vec4 calculatePointLight(PointLight pointLight, Material material, vec3 normal, vec4 position)
{
	vec3 lightDirection = position.xyz - pointLight.position;
	float distance = length(lightDirection);

	if(distance > pointLight.range) {
		return vec4(0.0);
	}

	lightDirection = normalize(lightDirection);

	vec4 color = calculateLight(pointLight.base, material, lightDirection, normal, position);

	float attenuation = pointLight.range / (distance * distance);
	/*
	const float threshold = 0.01;

	attenuation = clamp((attenuation - threshold) / (1.0 - threshold), 0.0, 1.0);
	*/

	return color * attenuation;
}

vec4 calculateSpotLight(SpotLight spotLight, Material material, vec3 normal, vec4 position)
{
	vec3 lightToFrag = normalize(position.xyz - spotLight.base.position);
	float spotFactor = dot(lightToFrag, spotLight.direction);

	if(spotFactor > spotLight.cutoff) {
		vec4 color = calculatePointLight(spotLight.base, material, normal, position);

		return color * (1.0 - (1.0 - spotFactor) / (1.0 - spotLight.cutoff));
	} else {
		return vec4(0.0);
	}
}

vec4 calculateDirectionalLight(DirectionalLight directionalLight, Material material, vec3 normal, vec4 position)
{
    return calculateLight(directionalLight.base, material, -directionalLight.direction, normal, position);
}

float linstep(float x, float low, float high)
{
    return clamp((x - low) / (high - low), 0.0, 1.0);
}

float calculateShadowFactor(sampler2D shadowMap, vec4 shadowMapCoords, float varianceMin, float bleed)
{
    vec3 coords = shadowMapCoords.xyz / shadowMapCoords.w;
    float compare = coords.z;

    vec2 moments = vec2(1.0) - texture(shadowMap, coords.xy).xy;

    float p = step(compare, moments.x);
    float variance = max(moments.y - moments.x * moments.x, varianceMin);

    float d = compare - moments.x;
    float pMax = linstep(variance / (variance + d * d), bleed, 1.0);

    return min(max(p, pMax), 1.0);
}

vec4 calculateCookie(sampler2D cookie, vec4 shadowMapCoords, float cookieScale)
{
    vec3 coords = shadowMapCoords.xyz / shadowMapCoords.w;
    vec4 color = texture(cookie, coords.xy * cookieScale);

    return color;
}

in vec2 vUV;
in vec3 vNormal;
in vec4 vPosition;
in vec4 vShadowMapCoords;

out vec4 vDiffuseColor;

uniform Material material;
uniform sampler2D diffuse;
uniform sampler2D shadowMap;
uniform sampler2D cookie;
uniform float shadowVarianceMin;
uniform float lightBleed;
uniform float cookieScale;
