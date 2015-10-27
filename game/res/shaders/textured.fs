#version 330

in vec2 vertexUV;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
  fragColor = vec4(texture(textureSampler, vertexUV).rgb, 1.0);
}
