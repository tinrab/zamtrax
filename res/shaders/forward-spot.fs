#version 330

struct Light
{
	vec4 color;
	float intensity;
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

in vec2 vUV;
in vec3 vNormal;
in vec4 vPosition;

out vec4 vDiffuseColor;

const float shininess = 0.5;
const float specularIntensity = 1.0;

uniform SpotLight spotLight;
uniform sampler2D diffuse;

vec4 calculateLight(Light light, vec3 lightDirection)
{
	float diffuseFactor = dot(vNormal, -lightDirection);

	vec4 diffuseColor = vec4(0.0);
	vec4 specularColor = vec4(0.0);

	diffuseColor = light.color * light.intensity * diffuseFactor;

	vec3 vertexToEye = normalize(-vPosition.xyz);
	vec3 reflectionDirection = reflect(-lightDirection, vNormal);
	float specularFactor = max(0.0, dot(vertexToEye, reflectionDirection));

	specularFactor = pow(specularFactor, shininess);
	specularColor = light.color * specularFactor * specularIntensity;

	return diffuseColor;
}

vec4 calculatePointLight()
{
	vec3 lightDirection = vPosition.xyz - spotLight.base.position;
	float distance = length(lightDirection);

	if(distance > spotLight.base.range) {
		return vec4(0.0);
	}

	lightDirection = normalize(lightDirection);

	vec4 color = calculateLight(spotLight.base.base, lightDirection);

	float attenuation = 1 / (distance * distance);
	const float threshold = 0.01;

	attenuation = clamp((attenuation - threshold) / (1.0 - threshold), 0.0, 1.0);

	return color * attenuation;
}

vec4 calculateSpotLight()
{
	vec3 lightToFrag = normalize(vPosition.xyz - spotLight.base.position);
	float spotFactor = dot(lightToFrag, spotLight.direction);

	if(spotFactor > spotLight.cutoff) {
		vec4 color = calculatePointLight();

		return color * (1.0 - (1.0 - spotFactor) * 1.0 / (1.0 - spotLight.cutoff));
	} else {
		return vec4(0.0);
	}
}

void main()
{
	vDiffuseColor = texture2D(diffuse, vUV) * calculateSpotLight();
}
