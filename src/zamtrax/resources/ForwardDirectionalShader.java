package zamtrax.resources;

import zamtrax.*;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class ForwardDirectionalShader extends ShaderProgram {

	private static ForwardDirectionalShader instance;

	public ForwardDirectionalShader(CharSequence vertexShaderSource, CharSequence fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		super(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
	}

	public static ForwardDirectionalShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forward-directional.vs", ForwardDirectionalShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forward-directional.fs", ForwardDirectionalShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.bind(AttributeType.UV, 1, "uv")
					.bind(AttributeType.NORMAL, 2, "normal")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("M"));
			uniforms.add(new Uniform("MVP"));
			uniforms.add(new Uniform("directionalLight.base.color"));
			uniforms.add(new Uniform("directionalLight.base.intensity"));
			uniforms.add(new Uniform("directionalLight.direction"));

			instance = new ForwardDirectionalShader(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	public void updateUniforms(Transform transform, Matrix4 viewProjection, Material material, DirectionalLight directionalLight) {
		material.getDiffuse().bind();

		Matrix4 model = transform.getLocalToWorldMatrix();
		Matrix4 mvp = viewProjection.mul(model);

		setUniform("M", true, model);
		setUniform("MVP", true, mvp);

		setUniform("directionalLight.base.color", directionalLight.getColor());
		setUniform("directionalLight.base.intensity", directionalLight.getIntensity());
		setUniform("directionalLight.direction", directionalLight.getTransform().forward());
	}

}
