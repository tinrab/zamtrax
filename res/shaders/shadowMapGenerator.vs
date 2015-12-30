#version 330

in layout(location = 0) vec4 position;

uniform mat4 MVP;

void main()
{
	gl_Position = MVP * position;
}
