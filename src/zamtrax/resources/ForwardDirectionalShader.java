package zamtrax.resources;

import zamtrax.*;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.Renderer;
import zamtrax.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class ForwardDirectionalShader extends ShaderProgram {

	private static ForwardDirectionalShader instance;

	private ForwardDirectionalShader(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		super(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
	}

	public static ForwardDirectionalShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forwardDirectional.vs", ForwardDirectionalShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forwardDirectional.fs", ForwardDirectionalShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.bind(AttributeType.UV, 1, "uv")
					.bind(AttributeType.NORMAL, 2, "normal")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("M"));
			uniforms.add(new Uniform("MVP"));
			uniforms.add(new Uniform("material.shininess"));
			uniforms.add(new Uniform("material.specularIntensity"));
			uniforms.add(new Uniform("diffuse"));
			uniforms.add(new Uniform("shadowMap"));

			uniforms.add(new Uniform("directionalLight.base.color"));
			uniforms.add(new Uniform("directionalLight.base.intensity"));
			uniforms.add(new Uniform("directionalLight.direction"));

			uniforms.add(new Uniform("MLVP"));

			instance = new ForwardDirectionalShader(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	public void updateUniforms(Renderer renderer, Matrix4 viewProjection, DirectionalLight directionalLight, Matrix4 lightViewProjection) {
		Material material = renderer.getMaterial();
		Matrix4 model = renderer.getTransform().getLocalToWorldMatrix();
		Matrix4 mvp = viewProjection.mul(model);

		setUniform("M", model);
		setUniform("MVP", mvp);
		setUniform("material.shininess", material.getShininess());
		setUniform("material.specularIntensity", material.getSpecularIntensity());

		setUniform("diffuse", 0);

		setUniform("directionalLight.base.color", directionalLight.getColor());
		setUniform("directionalLight.base.intensity", directionalLight.getIntensity());
		setUniform("directionalLight.direction", directionalLight.getTransform().forward());

		if (directionalLight.getShadows() == Light.Shadows.HARD) {
			setUniform("shadowMap", 1);
			setUniform("MLVP", lightViewProjection.mul(model));
		}
	}

}
