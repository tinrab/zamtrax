package zamtrax.resources;

import zamtrax.Resources;

import java.util.ArrayList;
import java.util.List;

public class Material {

	public static class TextureMap {

		private String name;
		private Texture texture;

		private TextureMap(String name, Texture texture) {
			this.name = name;
			this.texture = texture;
		}

		public String getName() {
			return name;
		}

		public Texture getTexture() {
			return texture;
		}

	}

	private Shader shader;
	private List<TextureMap> textures;
	private float shininess;
	private float specularIntensity;

	public Material(String vertexShaderPathname, String fragmentShaderPathname) {
		this(new Shader.Builder()
				.setVertexShaderSource(Resources.loadText(vertexShaderPathname))
				.setFragmentShaderSource(Resources.loadText(fragmentShaderPathname))
				.build());
	}

	public Material(Shader shader) {
		this.shader = shader;
		shininess = 0.5f;
		specularIntensity = 0.5f;

		textures = new ArrayList<>();
	}

	public List<TextureMap> getTextures() {
		return textures;
	}

	public void setTexture(String name, Texture texture) {
		textures.add(new TextureMap(name, texture));
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getSpecularIntensity() {
		return specularIntensity;
	}

	public void setSpecularIntensity(float specularIntensity) {
		this.specularIntensity = specularIntensity;
	}

	public Shader getShader() {
		return shader;
	}

}
