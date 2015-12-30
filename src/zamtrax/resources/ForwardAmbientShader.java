package zamtrax.resources;

import zamtrax.Color;
import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class ForwardAmbientShader extends ShaderProgram {

	private static ForwardAmbientShader instance;

	public ForwardAmbientShader(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		super(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
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

			instance = new ForwardAmbientShader(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	public void updateUniforms(Transform transform, Matrix4 viewProjection, Material material, Color ambientIntensity) {
		material.getDiffuse().bind();

		Matrix4 modelView = transform.getLocalToWorldMatrix();
		Matrix4 mvp = viewProjection.mul(modelView);

		setUniform("MVP", mvp);
		setUniform("ambientIntensity", ambientIntensity);
	}

}
