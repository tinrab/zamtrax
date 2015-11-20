#version 330

const int MAX_POINT_LIGHTS = 8;
const int MAX_SPOT_LIGHTS = 2;

in vec2 vUV;
in vec3 vNormal;
in vec4 vPosition;

out vec4 vFragColor;

uniform sampler2D uSampler;

struct Light
{
	vec3 color;
	float ambientIntensity;
	float diffuseIntensity;
};

struct Attenuation
{
	float constant;
	float linear;
	float exponential;
};

struct PointLight
{
	Light light;
	vec3 position;
	Attenuation attenuation;
};

struct SpotLight
{
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};

uniform int pointLightCount;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform int spotLightCount;
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

const vec3 ambientLight = vec3(0.1, 0.1, 0.1);
const float shininess = 0.5;
const float specularIntensity = 1.0;

vec4 calculateLight(Light light, vec3 lightDirection)
{
	vec4 ambientColor = vec4(light.color * light.ambientIntensity, 1.0);
	float diffuseFactor = dot(vNormal, -lightDirection);
	
	vec4 diffuseColor = vec4(0.0);
	vec4 specularColor = vec4(0.0);
	
	if(diffuseFactor > 0.0) {
		diffuseColor = vec4(light.color * light.diffuseIntensity * diffuseFactor, 1.0);
	
		vec3 vertexToEye = normalize(-vPosition.xyz);
		vec3 reflectionDirection = reflect(-lightDirection, vNormal);
		float specularFactor = dot(vertexToEye, reflectionDirection);
	
		if(specularFactor > 0) {
			specularFactor = pow(specularFactor, shininess);
			specularColor = vec4(light.color * specularFactor * specularIntensity, 1.0);
		}
	}
	
	return ambientColor + diffuseColor + specularColor;
}

vec4 calculatePointLight(PointLight pointLight)
{
	vec3 lightDirection = vPosition.xyz - pointLight.position;
	float distance = length(lightDirection);
	lightDirection = normalize(lightDirection);
	
	vec4 color = calculateLight(pointLight.light, lightDirection);
	
	float attenuation = pointLight.attenuation.constant +
						pointLight.attenuation.linear * distance +
						pointLight.attenuation.exponential * distance * distance;
						
	return color / attenuation;
}

vec4 calculateSpotLight(SpotLight spotLight)
{
	vec3 lightToFrag = normalize(vPosition.xyz - spotLight.pointLight.position);
	float spotFactor = dot(lightToFrag, spotLight.direction);
	
	if(spotFactor > spotLight.cutoff) {
		vec4 color = calculatePointLight(spotLight.pointLight);
		
		return color * (1.0 - (1.0 - spotFactor) * 1.0 / (1.0 - spotLight.cutoff));
	} else {
		return vec4(0.0);
	}
}

void main()
{
	vec4 textureColor = texture2D(uSampler, vUV);
	vec4 light = vec4(ambientLight, 1.0);
	
	for(int i = 0; i < pointLightCount; i++) {
		light += calculatePointLight(pointLights[i]);
	}
	
	for(int i = 0; i < spotLightCount; i++) {
		light += calculateSpotLight(spotLights[i]);
	}
	
	vFragColor = textureColor * light;
}
