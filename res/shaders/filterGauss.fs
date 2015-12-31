#version 330

in vec2 vUV;

out vec4 vDiffuseColor;

uniform vec3 blurScale;
uniform sampler2D filterTexture;

void main()
{
    vec4 color = vec4(0.0);

    color += texture(filterTexture, vUV + vec2(-3.0) * blurScale.xy) * (1.0  / 64.0);
    color += texture(filterTexture, vUV + vec2(-2.0) * blurScale.xy) * (6.0  / 64.0);
    color += texture(filterTexture, vUV + vec2(-1.0) * blurScale.xy) * (15.0 / 64.0);
    color += texture(filterTexture, vUV + vec2(0.0)  * blurScale.xy) * (20.0 / 64.0);
    color += texture(filterTexture, vUV + vec2(1.0)  * blurScale.xy) * (15.0 / 64.0);
    color += texture(filterTexture, vUV + vec2(2.0)  * blurScale.xy) * (6.0  / 64.0);
    color += texture(filterTexture, vUV + vec2(3.0)  * blurScale.xy) * (1.0  / 64.0);

    vDiffuseColor = color;
}
