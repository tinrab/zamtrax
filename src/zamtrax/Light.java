package zamtrax;

public final class Light extends Component {

	public enum Type {

		DIRECTIONAL, POINT, SPOT

	}

	private Color color;
	private float intensity;
	private float range;
	private float spotAngle;
	private Type lightType;

	private Transform transform;

	@Override
	public void onAdd() {
		color = Color.createWhite();
		intensity = 1.0f;
		range = 10.0f;
		spotAngle = 30.0f;
		lightType = Type.POINT;

		transform = getTransform();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getSpotAngle() {
		return spotAngle;
	}

	public void setSpotAngle(float spotAngle) {
		this.spotAngle = spotAngle;
	}

	public Type getLightType() {
		return lightType;
	}

	public void setLightType(Type lightType) {
		this.lightType = lightType;
	}

}
