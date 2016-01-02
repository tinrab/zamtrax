package zamtrax.rendering;

import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.resources.Shader;
import zamtrax.resources.Texture;

public class Filter {

	private Shader shader;

	public Filter(String vertexShaderPathname, String fragmentShaderPathname) {
		shader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText(vertexShaderPathname, Filter.class.getClassLoader()))
				.setFragmentShaderSource(Resources.loadText(fragmentShaderPathname, Filter.class.getClassLoader()))
				.build();
	}

	public void bind() {
		shader.bind();
	}

	public void updateUniforms(Texture source, RenderState renderState) {
		Matrix4 mvp = Matrix4.createIdentity();

		shader.setUniform("MVP", mvp);
		shader.setUniform("filterTexture", 0);

		source.bind(0);
	}

	public void release() {
		shader.release();
	}

}
