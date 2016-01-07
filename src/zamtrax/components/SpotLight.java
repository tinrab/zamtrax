package zamtrax.components;

import zamtrax.Mathf;
import zamtrax.Matrix4;

public final class SpotLight extends Light {

	private float cutoff;
	private float range;

	@Override
	public void onAdd() {
		super.onAdd();

		range = 10.0f;
		setSpotAngle(30.0f);
	}

	public float getCutoff() {
		return cutoff;
	}

	public void setSpotAngle(float angle) {
		cutoff = Mathf.fastCos(Mathf.DEG_TO_RAD * angle * 0.5f);
		setShadowProjection(Matrix4.createPerspective(angle, 1.0f, 0.01f, range));
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
		setShadowProjection(Matrix4.createPerspective(Mathf.acos(cutoff) * 2.0f * Mathf.RAD_TO_DEG, 1.0f, 0.01f, range));
	}

}
