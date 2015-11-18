#version 330

in vec2 vUV;
in vec3 vNormal;
in vec4 vPosition;

out vec4 vFragColor;

uniform sampler2D uSampler;

const vec3 ambientLight = vec3(0.0, 0.0, 0.0);
const vec3 lightPosition = vec3(0.0, 3.0, 0.0);
const vec3 lightColor = vec3(0.4, 0.8, 1.0);

void main()
{
	vec4 textureColor = texture2D(uSampler, vUV);
	
	vec3 lightDirection = normalize(lightPosition - vPosition.xyz);
	float lightIntensity = max(dot(vNormal, lightDirection), 0.0);
	
	vec3 light = ambientLight + lightColor * lightIntensity;
	
	vFragColor.a = textureColor.a;
	vFragColor.rgb = textureColor.rgb * light;
}
