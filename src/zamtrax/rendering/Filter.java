package zamtrax.rendering;

import zamtrax.Matrix4;
import zamtrax.resources.*;

import java.util.List;

public class Filter extends Shader {

	public void init(String vertexShaderSource, String fragmentShaderSource, List<Uniform> uniforms) {
		BindingInfo bindingInfo = new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.build();

		uniforms.add(new Uniform("MVP"));
		uniforms.add(new Uniform("filterTexture"));

		super.init(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
	}

	public void updateUniforms(Texture source, RenderState renderState) {
		Matrix4 mvp = Matrix4.createIdentity();

		setUniform("MVP", mvp);
		setUniform("filterTexture", 0);

		source.bind(0);
	}

}
