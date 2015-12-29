package zamtrax.resources;

import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.components.DirectionalLight;
import zamtrax.components.PointLight;
import zamtrax.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class ForwardPointShader extends ShaderProgram {

	private static ForwardPointShader instance;

	public ForwardPointShader(CharSequence vertexShaderSource, CharSequence fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		super(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
	}

	public static ForwardPointShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forward-point.vs", ForwardPointShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forward-point.fs", ForwardPointShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.bind(AttributeType.UV, 1, "uv")
					.bind(AttributeType.NORMAL, 2, "normal")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("M"));
			uniforms.add(new Uniform("MVP"));
			uniforms.add(new Uniform("pointLight.base.color"));
			uniforms.add(new Uniform("pointLight.base.intensity"));
			uniforms.add(new Uniform("pointLight.range"));
			uniforms.add(new Uniform("pointLight.position"));

			instance = new ForwardPointShader(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	public void updateUniforms(Transform transform, Matrix4 viewProjection, Material material, PointLight pointLight) {
		material.getDiffuse().bind();

		Matrix4 model = transform.getLocalToWorldMatrix();
		Matrix4 mvp = viewProjection.mul(model);

		setUniform("M", true, model);
		setUniform("MVP", true, mvp);

		setUniform("pointLight.base.color", pointLight.getColor());
		setUniform("pointLight.base.intensity", pointLight.getIntensity());
		setUniform("pointLight.range", pointLight.getRange());
		setUniform("pointLight.position", pointLight.getTransform().getPosition());
	}

}
