#version 330

#attribute position
#attribute color
#attribute uv

out vec4 vColor;
out vec2 vUV;

uniform mat4 P;

void main()
{
	vColor = color;
	vUV = uv;

	gl_Position = P * position;
}
