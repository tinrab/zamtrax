#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec4 color;

out vec4 vColor;

uniform mat4 MVP;

void main()
{
	vColor = color;

	gl_Position = MVP * position;
}
