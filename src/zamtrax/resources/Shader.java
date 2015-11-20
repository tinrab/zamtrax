package zamtrax.resources;

import zamtrax.Matrix3;
import zamtrax.Matrix4;
import zamtrax.Vector3;
import zamtrax.lights.PointLight;
import zamtrax.lights.SpotLight;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public interface Shader {

	void bind();

	void setUniform(CharSequence name, float value);

	void setUniform(CharSequence name, FloatBuffer floatBuffer);

	void setUniform(CharSequence name, int value);

	void setUniform(CharSequence name, Matrix4 value);

	void setUniform(CharSequence name, Matrix3 value);

	void setUniform(CharSequence name, Vector3 value);

	void setPointLights(List<PointLight> pointLights);

	void setSpotLights(List<SpotLight> spotLights);

	void release();

	void dispose();

	class Builder {

		private CharSequence vertexShaderSource;
		private CharSequence fragmentShaderSource;
		private BindingInfo bindingInfo;
		private List<Uniform> uniforms;

		public Builder() {
			uniforms = new ArrayList<>();
		}

		public Builder setVertexShaderSource(CharSequence vertexShaderSource) {
			this.vertexShaderSource = vertexShaderSource;

			return this;
		}

		public Builder setFragmentShaderSource(CharSequence fragmentShaderSource) {
			this.fragmentShaderSource = fragmentShaderSource;

			return this;
		}

		public Builder setBindingInfo(BindingInfo bindingInfo) {
			this.bindingInfo = bindingInfo;

			return this;
		}

		public Builder addUniform(CharSequence name) {
			uniforms.add(new Uniform(name));

			return this;
		}

		public Builder addTransformationUniforms() {
			addUniform("P");
			addUniform("MV");
			addUniform("N");

			return this;
		}

		public Builder addLightsUniforms() {
			addUniform("pointLightCount");
			addUniform("spotLightCount");

			for (int i = 0; i < ShaderProgram.MAX_POINT_LIGHTS; i++) {
				addUniform(String.format("pointLights[%d].light.color", i));
				addUniform(String.format("pointLights[%d].light.ambientIntensity", i));
				addUniform(String.format("pointLights[%d].light.diffuseIntensity", i));
				addUniform(String.format("pointLights[%d].position", i));
				addUniform(String.format("pointLights[%d].attenuation.constant", i));
				addUniform(String.format("pointLights[%d].attenuation.linear", i));
				addUniform(String.format("pointLights[%d].attenuation.exponential", i));
			}

			for (int i = 0; i < ShaderProgram.MAX_SPOT_LIGHTS; i++) {
				addUniform(String.format("spotLights[%d].pointLight.light.color", i));
				addUniform(String.format("spotLights[%d].pointLight.light.ambientIntensity", i));
				addUniform(String.format("spotLights[%d].pointLight.light.diffuseIntensity", i));
				addUniform(String.format("spotLights[%d].pointLight.position", i));
				addUniform(String.format("spotLights[%d].pointLight.attenuation.constant", i));
				addUniform(String.format("spotLights[%d].pointLight.attenuation.linear", i));
				addUniform(String.format("spotLights[%d].pointLight.attenuation.exponential", i));
				addUniform(String.format("spotLights[%d].direction", i));
				addUniform(String.format("spotLights[%d].cutoff", i));
			}

			return this;
		}

		public Shader build() {
			Shader shader = new ShaderProgram(vertexShaderSource, fragmentShaderSource, bindingInfo, uniforms);

			return shader;
		}

	}

}
