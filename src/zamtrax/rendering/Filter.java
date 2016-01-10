package zamtrax.rendering;

import zamtrax.Matrix4;
import zamtrax.Resources;
import zamtrax.Vector2;
import zamtrax.resources.Shader;
import zamtrax.resources.Texture;

public class Filter {

	private static Matrix4 mvp = Matrix4.createIdentity();

	protected Shader shader;
	protected boolean enabled;

	public Filter(String fragmentShaderPathname) {
		String fs = Resources.loadText(fragmentShaderPathname, Filter.class.getClassLoader());

		fs = "#texture filterTexture\n#texture depthTexture\nuniform vec2 textureSize;\n" + fs;

		shader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("filters/filter.vs", Filter.class.getClassLoader()))
				.setFragmentShaderSource(fs)
				.build();
		enabled = true;
	}

	public void bind() {
		shader.bind();
	}

	public void updateUniforms(Texture source, RenderState renderState) {
		shader.setUniform("MVP", mvp);
		shader.setUniform("filterTexture", 0);

		shader.setUniform("textureSize", new Vector2(source.getWidth(), source.getHeight()));

		source.bind(0);
	}

	public void release() {
		shader.release();
	}

	public Shader getShader() {
		return shader;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
