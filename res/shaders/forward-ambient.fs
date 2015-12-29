#version 330

in vec2 vUV;

out vec4 vDiffuseColor;

uniform vec4 ambientIntensity;
uniform sampler2D sampler;

void main()
{
    vDiffuseColor = texture2D(sampler, vUV) * ambientIntensity;
}
