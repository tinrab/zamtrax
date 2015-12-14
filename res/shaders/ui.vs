#version 330

in layout(location = 0) vec4 position;
in layout(location = 1) vec4 color;
in layout(location = 2) vec2 uv;

out vec4 vColor;
out vec2 vUV;

uniform mat4 P;

void main()
{
	vColor = color;
	vUV = uv;

	gl_Position = P * position;
}
