#texture filterTexture
#texture depthTexture

#shader
{
    return texture(filterTexture, vUV);
}
