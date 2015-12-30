#version 330

out vec4 vDiffuseColor;

void main()
{
   // maybe remove this
   vDiffuseColor = vec4(gl_FragCoord.z);
}
