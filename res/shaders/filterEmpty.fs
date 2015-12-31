#version 330

in vec2 vUV;

out vec4 vDiffuseColor;

uniform sampler2D filterTexture;
uniform sampler2D depthTexture;

void main()
{
	vDiffuseColor = texture(filterTexture, vUV);
}