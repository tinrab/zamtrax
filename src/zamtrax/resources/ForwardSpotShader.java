package zamtrax.resources;

import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.components.PointLight;
import zamtrax.components.SpotLight;
import zamtrax.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class ForwardSpotShader extends ShaderProgram {

	private static ForwardSpotShader instance;

	public ForwardSpotShader(CharSequence vertexShaderSource, CharSequence fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		super(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
	}

	public static ForwardSpotShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forward-spot.vs", ForwardSpotShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forward-spot.fs", ForwardSpotShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.bind(AttributeType.UV, 1, "uv")
					.bind(AttributeType.NORMAL, 2, "normal")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("M"));
			uniforms.add(new Uniform("MVP"));
			uniforms.add(new Uniform("spotLight.base.base.color"));
			uniforms.add(new Uniform("spotLight.base.base.intensity"));
			uniforms.add(new Uniform("spotLight.base.range"));
			uniforms.add(new Uniform("spotLight.base.position"));
			uniforms.add(new Uniform("spotLight.direction"));
			uniforms.add(new Uniform("spotLight.cutoff"));

			instance = new ForwardSpotShader(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	public void updateUniforms(Transform transform, Matrix4 viewProjection, Material material, SpotLight spotLight) {
		material.getDiffuse().bind();

		Matrix4 model = transform.getLocalToWorldMatrix();
		Matrix4 mvp = viewProjection.mul(model);

		setUniform("M", true, model);
		setUniform("MVP", true, mvp);

		setUniform("spotLight.base.base.color", spotLight.getColor());
		setUniform("spotLight.base.base.intensity", spotLight.getIntensity());
		setUniform("spotLight.base.range", spotLight.getRange());
		setUniform("spotLight.base.position", spotLight.getTransform().getPosition());
		setUniform("spotLight.direction", spotLight.getTransform().forward());
		setUniform("spotLight.cutoff", spotLight.getCutoff());
	}

}
