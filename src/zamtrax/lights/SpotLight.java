package zamtrax.lights;

import zamtrax.Mathf;

public final class SpotLight extends Light {

	private float cutoff;
	private float range;

	public SpotLight() {
		setSpotAngle(30.0f);
		range = 5.0f;
	}

	public float getCutoff() {
		return cutoff;
	}

	public void setSpotAngle(float cutoff) {
		this.cutoff = Mathf.cos(Mathf.DEG_TO_RAD * cutoff);
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

}
