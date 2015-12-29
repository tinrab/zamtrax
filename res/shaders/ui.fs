#version 330

in vec4 vColor;
in vec2 vUV;

out vec4 vDiffuseColor;

uniform sampler2D diffuseTexture;

void main()
{
	vDiffuseColor = texture(diffuseTexture, vUV) * vColor;
}
