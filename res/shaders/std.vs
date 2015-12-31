in layout(location = 0) vec4 position;
in layout(location = 1) vec2 uv;
in layout(location = 2) vec3 normal;

out vec2 vUV;
out vec3 vNormal;
out vec4 vPosition;
out vec4 vShadowMapCoords;

uniform mat4 M;
uniform mat4 MVP;
uniform mat4 modelLightViewProjection;

void main()
{
    vUV = uv;
    vNormal = (M * vec4(normal, 0.0)).xyz;
    vPosition = M * position;
    vShadowMapCoords = modelLightViewProjection * position;

    gl_Position = MVP * position;
}
