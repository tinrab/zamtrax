#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec4 color;
in layout(location = 2) mat4 MVP;

out vec4 vCOLOR;

void main()
{
    vCOLOR = color;

    gl_Position = MVP * position;
}
