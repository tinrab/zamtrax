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

vec4 calculateDirectionalLight(DirectionalLight directionalLight, Material material, vec3 normal, vec4 position)
{
    return calculateLight(directionalLight.base, material, -directionalLight.direction, normal, position);
}

float sampleShadowMap(sampler2D shadowMap, vec2 coords, float compare)
{
    return step(compare, texture(shadowMap, coords).r);
}

float calculateShadowFactor(sampler2D shadowMap, vec4 shadowMapCoords)
{
    vec3 coords = shadowMapCoords.xyz / shadowMapCoords.w;

    return sampleShadowMap(shadowMap, coords.xy, coords.z);
}

in vec2 vUV;
in vec3 vNormal;
in vec4 vPosition;
in vec4 vShadowMapCoords;

out vec4 vDiffuseColor;

uniform Material material;
uniform sampler2D diffuse;
uniform sampler2D shadowMap;
