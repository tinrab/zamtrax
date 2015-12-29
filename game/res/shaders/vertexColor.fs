#version 330

#ifndef MAX_POINT_LIGHTS
#define MAX_POINT_LIGHTS 8
#endif

#ifndef MAX_SPOT_LIGHTS
#define MAX_SPOT_LIGHTS 2
#endif

in vec4 vColor;
in vec3 vNormal;
in vec4 vPosition;

out vec4 vDiffuseColor;

struct Light
{
	vec3 color;
	float intensity;
};

struct PointLight
{
	Light light;
	vec3 position;
	float range;
};

struct SpotLight
{
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};

uniform vec3 ambientLight;
uniform int pointLightCount;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform int spotLightCount;
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

const float shininess = 0.5;
const float specularIntensity = 1.0;

vec3 calculateLight(Light light, vec3 lightDirection)
{
	float diffuseFactor = max(0.0, dot(vNormal, -lightDirection));
	
	vec3 diffuseColor = vec3(0.0);
	vec3 specularColor = vec3(0.0);
	
	diffuseColor = vec3(light.color * light.intensity * diffuseFactor);

	vec3 vertexToEye = normalize(-vPosition.xyz);
	vec3 reflectionDirection = reflect(-lightDirection, vNormal);
	float specularFactor = max(0.0, dot(vertexToEye, reflectionDirection));

	specularFactor = pow(specularFactor, shininess);
	specularColor = vec3(light.color * specularFactor * specularIntensity);
	
	return diffuseColor + specularColor;
}

vec3 calculatePointLight(PointLight pointLight)
{
	vec3 lightDirection = vPosition.xyz - pointLight.position;
	float distance = length(lightDirection);
	
	if(distance > pointLight.range) {
		return vec3(0.0);
	}
	
	lightDirection = normalize(lightDirection);
	
	vec3 color = calculateLight(pointLight.light, lightDirection);

	float attenuation = 1 / (distance * distance);
	const float threshold = 0.01;
	
	attenuation = clamp((attenuation - threshold) / (1.0 - threshold), 0.0, 1.0);

	return color * attenuation;
}

vec3 calculateSpotLight(SpotLight spotLight)
{
	vec3 lightToFrag = normalize(vPosition.xyz - spotLight.pointLight.position);
	float spotFactor = dot(lightToFrag, spotLight.direction);
	
	if(spotFactor > spotLight.cutoff) {
		vec3 color = calculatePointLight(spotLight.pointLight);
		
		return color * (1.0 - (1.0 - spotFactor) * 1.0 / (1.0 - spotLight.cutoff));
	} else {
		return vec3(0.0);
	}
}

void main()
{
	vec3 light = ambientLight;
	
	for(int i = 0; i < pointLightCount; i++) {
		if(pointLights[i].light.intensity > 0.0) {
			light += calculatePointLight(pointLights[i]);
		}
	}
	
	for(int i = 0; i < spotLightCount; i++) {
	    if(spotLights[i].pointLight.light.intensity > 0.0) {
		    light += calculateSpotLight(spotLights[i]);
		}
	}

	vDiffuseColor = vColor * vec4(light, 1.0);
}
