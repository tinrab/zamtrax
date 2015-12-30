package zamtrax.resources;

public class Material {

	private Texture diffuse;
	private float shininess;
	private float specularIntensity;

	public Material(Texture diffuse) {
		this.diffuse = diffuse;
		shininess = 0.5f;
		specularIntensity = 0.5f;
	}

	public Texture getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Texture diffuse) {
		this.diffuse = diffuse;
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

}
