#texture diffuse

#shader
{
	return texture2D(diffuse, vUV) * vCOLOR;
}
