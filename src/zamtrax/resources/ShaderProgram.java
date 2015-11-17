package zamtrax.resources;

import zamtrax.Matrix3;
import zamtrax.Matrix4;
import zamtrax.Vector3;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

class ShaderProgram implements Shader {

	private ShaderResource resource;
	private int program;

	public ShaderProgram(CharSequence vertexShaderSource, CharSequence fragmentShaderSource, BindingInfo bindingInfo, List<Uniform> uniforms) {
		StringBuilder sb = new StringBuilder();
		sb.append(vertexShaderSource);
		sb.append(fragmentShaderSource);

		resource = ShaderResource.create(sb.toString().hashCode());

		resource.setBindingInfo(bindingInfo);
		resource.setUniforms(uniforms);

		program = resource.getProgram();

		compile(vertexShaderSource, GL_VERTEX_SHADER);
		compile(fragmentShaderSource, GL_FRAGMENT_SHADER);

		bindAttributes();

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
	public void setUniform(CharSequence name, Matrix4 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniformMatrix4fv(uniform.getLocation(), true, value.toBuffer());
	}

	@Override
	public void setUniform(CharSequence name, Vector3 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniform3f(uniform.getLocation(), value.x, value.y, value.z);
	}

	@Override
	public void setUniform(CharSequence name, Matrix3 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniformMatrix3fv(uniform.getLocation(), true, value.toBuffer());
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

}
