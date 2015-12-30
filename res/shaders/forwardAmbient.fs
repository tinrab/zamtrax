#version 330

in vec2 vUV;

out vec4 vDiffuseColor;

uniform vec4 ambientIntensity;
uniform sampler2D diffuse;

void main()
{
    vDiffuseColor = texture2D(diffuse, vUV) * ambientIntensity;
}
