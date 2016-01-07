package zamtrax.rendering;

import zamtrax.Vector3;
import zamtrax.resources.Texture;

public class GaussBlur extends Filter {

	private Vector3 blurScale;

	public GaussBlur() {
		super("filters/gauss.filter");

		blurScale = new Vector3();
	}

	@Override
	public void updateUniforms(Texture source, RenderState renderState) {
		super.updateUniforms(source, renderState);

		shader.setUniform("blurScale", blurScale);
	}

	public void setBlurScale(Vector3 blurScale) {
		this.blurScale = blurScale;
	}

}
