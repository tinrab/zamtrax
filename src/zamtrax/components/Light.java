package zamtrax.components;

import zamtrax.Color;
import zamtrax.Component;
import zamtrax.Matrix4;

public abstract class Light extends Component {

	public enum Shadows {
		NONE, HARD, SOFT
	}

	private Color color;
	private float intensity;
	private Shadows shadows;
	private Matrix4 shadowProjection;

	@Override
	public void onAdd() {
		color = Color.createWhite();
		intensity = 1.0f;
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

	public Shadows getShadows() {
		return shadows;
	}

	public void setShadows(Shadows shadows) {
		this.shadows = shadows;
	}

	public Matrix4 getShadowProjection() {
		return shadowProjection;
	}

	protected void setShadowProjection(Matrix4 shadowProjection) {
		this.shadowProjection = shadowProjection;
	}

}
