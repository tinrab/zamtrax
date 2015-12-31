#version 330

in vec2 vUV;

out vec4 vDiffuseColor;

uniform sampler2D filterTexture;

void main()
{
    vDiffuseColor = texture(filterTexture, vUV);
}
