package fri.rg.zamtrax;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.rendering.Filter;
import zamtrax.rendering.RenderState;
import zamtrax.resources.Texture;

public class FlareFilter extends Filter {

	private Vector3 worldPosition;
	private float brightness;

	public FlareFilter() {
		super("shaders/flare.filter");

		worldPosition = new Vector3();
		brightness = 1.0f;
	}

	@Override
	public void updateUniforms(Texture source, RenderState renderState) {
		super.updateUniforms(source, renderState);

		Vector3 viewportPoint = Camera.getMainCamera().worldToViewportPoint(worldPosition);

		if (viewportPoint.z < 0.0f) {
			shader.setUniform("brightness", 0.0f);
		} else {
			shader.setUniform("brightness", brightness);
			shader.setUniform("position", new Vector2(viewportPoint.x, viewportPoint.y));
		}
	}

	public void setWorldPosition(Vector3 worldPosition) {
		this.worldPosition = worldPosition;
	}

	public Vector3 getWorldPosition() {
		return worldPosition;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

	public float getBrightness() {
		return brightness;
	}

}
