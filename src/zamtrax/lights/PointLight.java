package zamtrax.lights;

public class PointLight extends Light {

	private Attenuation attenuation;

	public PointLight() {
		attenuation = new Attenuation(0.0f, 0.0f, 1.0f);
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

}
