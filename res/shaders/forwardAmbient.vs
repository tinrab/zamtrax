#version 330

#attribute position
#attribute uv
#attribute normal

out vec2 vUV;

uniform mat4 MVP;

void main()
{
	vUV = uv;

	gl_Position = MVP * position;
}
