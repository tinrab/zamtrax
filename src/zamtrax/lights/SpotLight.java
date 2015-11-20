package zamtrax.lights;

import zamtrax.Mathf;

public final class SpotLight extends Light {

	private float cutoff;
	private Attenuation attenuation;

	public SpotLight() {
		cutoff = Mathf.cos(Mathf.DEG_TO_RAD * 30.0f);
		attenuation = new Attenuation(0.0f, 1.0f, 0.0f);
	}

	public float getCutoff() {
		return cutoff;
	}

	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

}
