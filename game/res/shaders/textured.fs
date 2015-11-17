#version 330
precision mediump float;

in highp vec2 vertexUV;
in vec3 vertexNormal;
in vec4 vertexPosition;

out vec4 fragColor;

uniform sampler2D sampler;

const vec3 lightSource = vec3(3.0, 3.0, -5.0);
const vec3 ambientLight = vec3(0.0, 0.0, 0.0);
const float shininess = 0.4;

void main()
{
	vec4 textureColor = texture2D(sampler, vec2(vertexUV.s, vertexUV.t));
	
	vec3 vp = vertexPosition.xyz;
	vec3 surfaceNormal = normalize(vertexNormal.xyz);
	
	vec3 lightDirection = normalize(lightSource - vp);
	float diffuseLightIntensity = max(0.0, dot(surfaceNormal, lightDirection));
	
	fragColor.a = textureColor.a;
	fragColor.rgb = diffuseLightIntensity * textureColor.rgb;
	fragColor.rgb += ambientLight;
	
	/*
	vec3 reflectionDirection = normalize(reflect(-lightDirection, surfaceNormal));
	float specular = max(0.0, dot(surfaceNormal, reflectionDirection));
	
	if(diffuseLightIntensity != 0.0){
		float fspecular = pow(specular, shininess);
		fragColor += fspecular;
	}
	*/
}
