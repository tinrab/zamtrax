package zamtrax.resources;

final class BasicMaterial implements Material {

	private Shader shader;
	private Texture texture;

	public BasicMaterial(Shader shader, Texture texture) {
		this.shader = shader;
		this.texture = texture;
	}

	@Override
	public void bind() {
		shader.bind();
		texture.bind();
	}

	@Override
	public void unbind() {
		texture.release();
		shader.release();
	}

	@Override
	public Shader getShader() {
		return shader;
	}

	@Override
	public Texture getTexture() {
		return texture;
	}

}
