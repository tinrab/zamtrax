package zamtrax.resources;

final class ShadedMaterial implements Material {

	private Shader shader;

	ShadedMaterial(Shader shader) {
		this.shader = shader;
	}

	@Override
	public void bind() {
		shader.bind();
	}

	@Override
	public void unbind() {
		shader.release();
	}

	@Override
	public Shader getShader() {
		return shader;
	}

	@Override
	public Texture getTexture() {
		return null;
	}

}
