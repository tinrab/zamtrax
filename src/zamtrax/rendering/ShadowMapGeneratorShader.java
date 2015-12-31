package zamtrax.rendering;

import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.components.Renderer;
import zamtrax.resources.AttributeType;
import zamtrax.resources.BindingInfo;
import zamtrax.resources.Shader;
import zamtrax.resources.Uniform;

import java.util.ArrayList;
import java.util.List;

public class ShadowMapGeneratorShader extends Shader {

	private static ShadowMapGeneratorShader instance;

	private ShadowMapGeneratorShader() {
	}

	public static ShadowMapGeneratorShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/shadowMapGenerator.vs", ForwardDirectionalShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/shadowMapGenerator.fs", ForwardDirectionalShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("MVP"));

			instance = new ShadowMapGeneratorShader();
			instance.init(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(RenderState renderState) {
		Matrix4 viewProjection = renderState.getViewProjection();
		Renderer renderer = renderState.getRenderer();

		Matrix4 mvp = viewProjection.mul(renderer.getTransform().getLocalToWorldMatrix());

		setUniform("MVP", mvp);
	}

}
