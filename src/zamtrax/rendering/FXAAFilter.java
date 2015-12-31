package zamtrax.rendering;

import zamtrax.Resources;
import zamtrax.resources.Texture;
import zamtrax.resources.Uniform;

import java.util.ArrayList;
import java.util.List;

public class FXAAFilter extends Filter {

	private static FXAAFilter instance;

	private FXAAFilter() {
	}

	public static FXAAFilter getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/filterEmpty.vs", FXAAFilter.class.getClassLoader());
			String fs = Resources.loadText("shaders/filterFXAA.fs", FXAAFilter.class.getClassLoader());

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("depthTexture"));

			instance = new FXAAFilter();
			instance.init(vs, fs, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(Texture source, RenderState renderState) {
		super.updateUniforms(source, renderState);

		setUniform("depthTexture", 1);
		source.bind(1, 1);
	}

}
