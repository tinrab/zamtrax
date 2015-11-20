package zamtrax.resources;

import zamtrax.Resources;

public interface Material {

	void bind();

	void unbind();

	Shader getShader();

	Texture getTexture();

	class Builder {

		private Shader shader;
		private Texture texture;

		public Builder setShader(Shader shader) {
			this.shader = shader;

			return this;
		}

		public Builder setTexture(Texture texture) {
			this.texture = texture;

			return this;
		}

		public Material build() {
			if (texture == null) {
				return new ShadedMaterial(shader);
			} else {
				return new BasicMaterial(shader, texture);
			}
		}

	}

}
