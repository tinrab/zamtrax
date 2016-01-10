#texture diffuse

#shader
{
    return texture(diffuse, fract(vUV)) * vCOLOR;
}
