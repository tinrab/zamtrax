#version 330
precision mediump float;

in highp vec2 vertexUV;
in vec3 lightWeighting;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
  vec4 textureColor = texture2D(textureSampler, vec2(vertexUV.s, vertexUV.t));
	
  fragColor = vec4(textureColor.rgb * lightWeighting, textureColor.a);
}
