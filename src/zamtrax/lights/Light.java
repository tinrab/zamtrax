package zamtrax.lights;

import zamtrax.Color;
import zamtrax.Component;

public abstract class Light extends Component {

	private Color color;
	private float intensity;

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

}
