package zamtrax.rendering;

import zamtrax.Color;
import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.components.Renderer;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

public class ForwardAmbientShader extends Shader {

	private static ForwardAmbientShader instance;

	private ForwardAmbientShader() {
	}

	public static ForwardAmbientShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forwardAmbient.vs", ForwardAmbientShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forwardAmbient.fs", ForwardAmbientShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.bind(AttributeType.UV, 1, "uv")
					.bind(AttributeType.NORMAL, 2, "normal")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("MVP"));
			uniforms.add(new Uniform("ambientIntensity"));

			instance = new ForwardAmbientShader();
			instance.init(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(RenderState renderState) {
		Matrix4 viewProjection = renderState.getViewProjection();
		Renderer renderer = renderState.getRenderer();
		Material material = renderer.getMaterial();
		Color ambientIntensity = renderState.getAmbientIntenstiy();

		Matrix4 mvp = viewProjection.mul(renderer.getTransform().getLocalToWorldMatrix());

		material.getDiffuse().bind();

		setUniform("MVP", mvp);
		setUniform("ambientIntensity", ambientIntensity);
	}

}
