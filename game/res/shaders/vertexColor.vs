#version 330

in layout(location = 0) vec3 position;
in layout(location = 1) vec4 color;

out vec4 vertexColor;

uniform mat4 mvp;

void main()
{
    vertexColor = color;

    gl_Position = mvp * vec4(position, 1.0);
}
