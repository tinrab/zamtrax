package fri.rg.zamtrax.level;

import zamtrax.rendering.Filter;
import zamtrax.rendering.RenderState;
import zamtrax.resources.Texture;

public class HurtFilter extends Filter {

	private float intensity;

	public HurtFilter() {
		super("shaders/hurt.filter");
	}

	@Override
	public void updateUniforms(Texture source, RenderState renderState) {
		super.updateUniforms(source, renderState);

		shader.setUniform("intensity", intensity);
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getIntensity() {
		return intensity;
	}

}
