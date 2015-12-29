#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec2 uv;
in layout(location = 2) vec3 normal;

out vec2 vUV;

uniform mat4 MVP;

void main()
{
	vUV = uv;

	gl_Position = MVP * position;
}
