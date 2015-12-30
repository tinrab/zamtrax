package zamtrax.resources;

import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class ShadowMapGeneratorShader extends ShaderProgram {

	private static ShadowMapGeneratorShader instance;

	public ShadowMapGeneratorShader(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		super(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
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

			instance = new ShadowMapGeneratorShader(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	public void updateUniforms(Transform transform, Matrix4 viewProjection) {
		Matrix4 mvp = viewProjection.mul(transform.getLocalToWorldMatrix());

		setUniform("MVP", mvp);
	}

}
