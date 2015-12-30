package zamtrax.components;

public class PointLight extends Light {

	private float range;

	@Override
	public void onAdd() {
		super.onAdd();

		range = 5.0f;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

}
