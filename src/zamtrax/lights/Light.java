package zamtrax.lights;

import zamtrax.Color;
import zamtrax.Component;

public abstract class Light extends Component {

	private Color color;
	private float ambientIntensity;
	private float diffuseIntensity;

	@Override
	public void onAdd() {
		color = Color.createWhite();
		ambientIntensity = 1.0f;
		diffuseIntensity = 1.0f;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getAmbientIntensity() {
		return ambientIntensity;
	}

	public void setAmbientIntensity(float ambientIntensity) {
		this.ambientIntensity = ambientIntensity;
	}

	public float getDiffuseIntensity() {
		return diffuseIntensity;
	}

	public void setDiffuseIntensity(float diffuseIntensity) {
		this.diffuseIntensity = diffuseIntensity;
	}

}
