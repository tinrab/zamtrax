package zamtrax.resources;

import zamtrax.*;
import zamtrax.components.PointLight;
import zamtrax.components.SpotLight;

import java.io.StringReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

class ShaderProgram implements Shader {

	private ShaderResource resource;
	private int program;

	public ShaderProgram(String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		int id = (vertexShaderSource + fragmentShaderSource).hashCode();
		resource = ShaderResource.retain(id);

		if (resource == null) {
			resource = ShaderResource.alloc(id);
			program = resource.getProgram();

			resource.setBindingInfo(bindingInfo);
			resource.setUniforms(uniforms);

			vertexShaderSource = resolveIncludes(vertexShaderSource);
			fragmentShaderSource = resolveIncludes(fragmentShaderSource);

			compile(vertexShaderSource, GL_VERTEX_SHADER);
			compile(fragmentShaderSource, GL_FRAGMENT_SHADER);

			bindAttributes();

			link();

			bindUniforms();
		} else {
			program = resource.getProgram();
		}
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

		glAttachShader(resource.getProgram(), shader);
	}

	private void bindAttributes() {
		BindingInfo bindingInfo = resource.getBindingInfo();

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
		resource.getUniforms().values().forEach(u -> {
			int loc = glGetUniformLocation(program, u.getName());

			u.setLocation(loc);
		});
	}

	@Override
	public void setUniform(CharSequence name, float value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniform1f(uniform.getLocation(), value);
	}

	@Override
	public void setUniform(CharSequence name, FloatBuffer floatBuffer) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniform1fv(uniform.getLocation(), floatBuffer);
	}

	@Override
	public void setUniform(CharSequence name, int value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniform1i(uniform.getLocation(), value);
	}

	@Override
	public void setUniform(CharSequence name, Matrix4 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniformMatrix4fv(uniform.getLocation(), false, value.toBuffer());
	}

	@Override
	public void setUniform(CharSequence name, Vector3 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniform3f(uniform.getLocation(), value.x, value.y, value.z);
	}

	@Override
	public void setUniform(CharSequence name, Color value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniform4f(uniform.getLocation(), value.r, value.g, value.b, value.a);
	}

	@Override
	public void setUniform(CharSequence name, Matrix3 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniformMatrix3fv(uniform.getLocation(), false, value.toBuffer());
	}

	@Override
	public void bind() {
		glUseProgram(program);
	}

	@Override
	public void release() {
		glUseProgram(0);
	}

	@Override
	public void dispose() {
		resource.removeReference();
	}

	private static String resolveIncludes(String source) {
		List<String> lines = Arrays.asList(source.split("\n"));

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();

			if (line.startsWith("#include")) {
				String file = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
				String included = Resources.loadText(file, ShaderProgram.class.getClassLoader());

				lines.set(i, included);

				lines = Arrays.asList(String.join("\n", lines));
				i = 0;
			}
		}

		return String.join("\n", lines);
	}

}
