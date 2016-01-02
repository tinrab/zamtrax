package fri.rg.zamtrax.tests;

import zamtrax.Resources;
import zamtrax.resources.Shader;

import java.util.ArrayList;

public class ShaderPreprocessor {

	public static void main(String[] args) {
		/*
		Shader shader = new Shader();

		shader.init("#attribute position\n#attribute color\n", "", null, new ArrayList<>());
		*/

		new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("shaders/vertexColor.vs", ClassLoader.getSystemClassLoader()))
				.setFragmentShaderSource(Resources.loadText("shaders/vertexColor.fs", ClassLoader.getSystemClassLoader()));
	}

}
