package zamtrax.resources;

import zamtrax.*;
import zamtrax.rendering.RenderState;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Shader {

	private int program;
	private Map<String, Uniform> uniformMap;

	public Shader() {
	}

	public Shader(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo) {
		init(vertexShaderSource, fragmentShaderSource, bindingInfo, new ArrayList<>());
	}

	public Shader(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		init(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
	}

	public void init(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		uniformMap = new HashMap<>();

		for (Uniform uniform : uniforms) {
			uniformMap.put(uniform.getName(), uniform);
		}

		program = glCreateProgram();

		if (program == 0) {
			throw new RuntimeException(glGetProgramInfoLog(program));
		}

		vertexShaderSource = resolveIncludes(vertexShaderSource);
		fragmentShaderSource = resolveIncludes(fragmentShaderSource);

		compile(vertexShaderSource, GL_VERTEX_SHADER);
		compile(fragmentShaderSource, GL_FRAGMENT_SHADER);

		bindAttributes(bindingInfo);

		link();

		bindUniforms();
	}

	private void compile(CharSequence source, int type) {
		int shader = glCreateShader(type);

		if (shader == 0) {
			throw new Error("Failed to create shader");
		}

		glShaderSource(shader, source);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new Error(glGetShaderInfoLog(shader));
		}

		glAttachShader(program, shader);
	}

	private void bindAttributes(BindingInfo bindingInfo) {
		bindingInfo.getAttributePointers().forEach(ap -> glBindAttribLocation(program, ap.getLocation(), ap.getName()));
	}

	private void link() {
		glLinkProgram(program);

		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			throw new Error(glGetShaderInfoLog(program));
		}

		glValidateProgram(program);

		if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
			throw new Error(glGetShaderInfoLog(program));
		}
	}

	private void bindUniforms() {
		uniformMap.values().forEach(u -> {
			int location = glGetUniformLocation(program, u.getName());

			u.setLocation(location);
		});
	}

	public void setUniform(CharSequence name, float value) {
		Uniform uniform = uniformMap.get(name);

		glUniform1f(uniform.getLocation(), value);
	}

	public void setUniform(CharSequence name, FloatBuffer floatBuffer) {
		Uniform uniform = uniformMap.get(name);

		glUniform1fv(uniform.getLocation(), floatBuffer);
	}

	public void setUniform(CharSequence name, int value) {
		Uniform uniform = uniformMap.get(name);

		glUniform1i(uniform.getLocation(), value);
	}

	public void setUniform(CharSequence name, Matrix4 value) {
		Uniform uniform = uniformMap.get(name);

		glUniformMatrix4fv(uniform.getLocation(), false, value.toBuffer());
	}

	public void setUniform(CharSequence name, Vector3 value) {
		Uniform uniform = uniformMap.get(name);

		glUniform3f(uniform.getLocation(), value.x, value.y, value.z);
	}

	public void setUniform(CharSequence name, Color value) {
		Uniform uniform = uniformMap.get(name);

		glUniform4f(uniform.getLocation(), value.r, value.g, value.b, value.a);
	}

	public void setUniform(CharSequence name, Matrix3 value) {
		Uniform uniform = uniformMap.get(name);

		glUniformMatrix3fv(uniform.getLocation(), false, value.toBuffer());
	}

	public void setUniform(String name, boolean value) {
		Uniform uniform = uniformMap.get(name);

		glUniform1i(uniform.getLocation(), value ? 1 : 0);
	}

	public void updateUniforms(RenderState renderState) {
	}

	public void bind() {
		glUseProgram(program);
	}

	public void release() {
		glUseProgram(0);
	}

	public void dispose() {
		glDeleteProgram(program);
	}

	private static String resolveIncludes(String source) {
		List<String> lines = Arrays.asList(source.split("\n"));

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();

			if (line.startsWith("#include")) {
				String file = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
				String included = Resources.loadText(file, Shader.class.getClassLoader());

				lines.set(i, included);

				lines = Arrays.asList(String.join("\n", lines));
				i = 0;
			}
		}

		return String.join("\n", lines);
	}

	public static class Builder {

		private String vertexShaderSource;
		private String fragmentShaderSource;
		private BindingInfo bindingInfo;
		private List<Uniform> uniforms;

		public Builder() {
			uniforms = new ArrayList<>();
		}

		public Builder setVertexShaderSource(String vertexShaderSource) {
			this.vertexShaderSource = vertexShaderSource;

			return this;
		}

		public Builder setFragmentShaderSource(String fragmentShaderSource) {
			this.fragmentShaderSource = fragmentShaderSource;

			return this;
		}

		public Builder setBindingInfo(BindingInfo bindingInfo) {
			this.bindingInfo = bindingInfo;

			return this;
		}

		public Builder addUniform(String name) {
			uniforms.add(new Uniform(name));

			return this;
		}

		public Shader build() {
			return new Shader(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);
		}

	}

}
