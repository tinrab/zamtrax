package zamtrax.resources;

import zamtrax.Color;
import zamtrax.Matrix3;
import zamtrax.Matrix4;
import zamtrax.Vector3;
import zamtrax.components.PointLight;
import zamtrax.components.SpotLight;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

class ShaderProgram implements Shader {

	public static final int MAX_POINT_LIGHTS = 8;
	public static final int MAX_SPOT_LIGHTS = 2;

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
	public void setUniform(CharSequence name, boolean normalize, Matrix4 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniformMatrix4fv(uniform.getLocation(), normalize, value.toBuffer());
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
	public void setUniform(CharSequence name, boolean normalize, Matrix3 value) {
		Uniform uniform = resource.getUniforms().get(name);

		glUniformMatrix3fv(uniform.getLocation(), normalize, value.toBuffer());
	}

	@Override
	public void setPointLights(List<PointLight> pointLights) {
		glUniform1i(resource.getUniforms().get("pointLightCount").getLocation(), pointLights.size());

		// this is pretty bad
		for (int i = 0; i < pointLights.size(); i++) {
			PointLight pointLight = pointLights.get(i);
			Color color = pointLight.getColor();
			float intensity = pointLight.getIntensity();
			float range = pointLight.getRange();
			Vector3 position = pointLight.getTransform().getPosition();

			glUniform3f(resource.getUniforms().get(String.format("pointLights[%d].light.color", i)).getLocation(), color.r, color.g, color.b);
			glUniform1f(resource.getUniforms().get(String.format("pointLights[%d].light.intensity", i)).getLocation(), intensity);
			glUniform1f(resource.getUniforms().get(String.format("pointLights[%d].range", i)).getLocation(), range);
			glUniform3f(resource.getUniforms().get(String.format("pointLights[%d].position", i)).getLocation(), position.x, position.y, position.z);
		}
	}

	@Override
	public void setSpotLights(List<SpotLight> spotLights) {
		glUniform1i(resource.getUniforms().get("spotLightCount").getLocation(), spotLights.size());

		// this is bad aswell
		for (int i = 0; i < spotLights.size(); i++) {
			SpotLight spotLight = spotLights.get(i);
			Color color = spotLight.getColor();
			float intensity = spotLight.getIntensity();
			float range = spotLight.getRange();
			Vector3 position = spotLight.getTransform().getPosition();
			float cutoff = spotLight.getCutoff();
			Vector3 direction = spotLight.getTransform().forward();

			glUniform3f(resource.getUniforms().get(String.format("spotLights[%d].pointLight.light.color", i)).getLocation(), color.r, color.g, color.b);
			glUniform1f(resource.getUniforms().get(String.format("spotLights[%d].pointLight.light.intensity", i)).getLocation(), intensity);
			glUniform3f(resource.getUniforms().get(String.format("spotLights[%d].pointLight.position", i)).getLocation(), position.x, position.y, position.z);
			glUniform1f(resource.getUniforms().get(String.format("spotLights[%d].pointLight.range", i)).getLocation(), range);
			glUniform3f(resource.getUniforms().get(String.format("spotLights[%d].direction", i)).getLocation(), direction.x, direction.y, direction.z);
			glUniform1f(resource.getUniforms().get(String.format("spotLights[%d].cutoff", i)).getLocation(), cutoff);
		}
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
