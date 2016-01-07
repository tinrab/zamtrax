package fri.rg.zamtrax;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.rendering.Filter;
import zamtrax.rendering.RenderState;
import zamtrax.resources.Texture;

public class FlareFilter extends Filter {

	private Vector3 worldPosition;

	public FlareFilter() {
		super("shaders/flare.filter");

		worldPosition = new Vector3();
	}

	@Override
	public void updateUniforms(Texture source, RenderState renderState) {
		Matrix4 mvp = Matrix4.createIdentity();

		shader.setUniform("MVP", mvp);
		shader.setUniform("filterTexture", 0);
		shader.setUniform("textureSize", new Vector2(Game.getScreenWidth(), Game.getScreenHeight()));
		shader.setUniform("time", (float) Time.getInstance().currentMillis());

		Vector3 viewportPoint = Camera.getMainCamera().worldToViewportPoint(worldPosition);

		if (viewportPoint.z < 0.0f) {
			shader.setUniform("brightness", 0.0f);
		} else {
			shader.setUniform("brightness", 1.0f);
			shader.setUniform("position", new Vector2(viewportPoint.x, viewportPoint.y));
		}
	}

	public void setWorldPosition(Vector3 worldPosition) {
		this.worldPosition = worldPosition;
	}

}
