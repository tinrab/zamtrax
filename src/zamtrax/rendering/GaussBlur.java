package zamtrax.rendering;

import zamtrax.Resources;
import zamtrax.resources.*;
import zamtrax.*;
import zamtrax.resources.BindingInfo;
import zamtrax.resources.Shader;

import java.util.ArrayList;
import java.util.List;

public class GaussBlur extends Filter {

	private static GaussBlur instance;

	private Vector3 blurScale;

	private GaussBlur() {
		blurScale = new Vector3();
	}

	public static GaussBlur getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/filterGauss.vs", GaussBlur.class.getClassLoader());
			String fs = Resources.loadText("shaders/filterGauss.fs", GaussBlur.class.getClassLoader());

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("blurScale"));

			instance = new GaussBlur();
			instance.init(vs, fs, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(Texture source, RenderState renderState) {
		super.updateUniforms(source, renderState);

		setUniform("blurScale", blurScale);
	}

	public void setBlurScale(Vector3 blurScale) {
		this.blurScale = blurScale;
	}

}
