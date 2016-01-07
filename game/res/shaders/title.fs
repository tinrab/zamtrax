#define near 0.01
#define far 100.0

const vec4 fogColor = vec4(0.0, 0.0, 0.0, 1.0);
const float FogDensity = 0.5;

vec4 mix(vec4 a, vec4 b, float t)
{
    return a * (1.0 - t) + b * t;
}

#shader
{
    float depth = (2.0 * near) / (far + near - gl_FragCoord.z * (far - near));

    float dist = 0;
    float fogFactor = 0;

    dist = (gl_FragCoord.z / gl_FragCoord.w);

    float start = 5;
    float end = 7;

    fogFactor = (end - dist)/(end - start);
    fogFactor = clamp( fogFactor, 0.0, 1.0 );

    return mix(fogColor, vCOLOR, fogFactor);
}
