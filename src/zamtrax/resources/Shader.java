package zamtrax.resources;

import zamtrax.*;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.Renderer;
import zamtrax.rendering.RenderState;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

	private int program;
	private Map<String, Uniform> uniformMap;
	private BindingInfo bindingInfo;
	private boolean enabledLights;
	private boolean castShadows, receiveShadows;

	protected Shader(boolean enabledLights, boolean castShadows, boolean receiveShadows, String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<String> uniformNames) {
		this.enabledLights = enabledLights;
		this.castShadows = castShadows;
		this.receiveShadows = receiveShadows;
		this.bindingInfo = bindingInfo;
		uniformMap = new HashMap<>();

		uniformNames.forEach(name -> uniformMap.put(name, new Uniform(name)));

		program = glCreateProgram();

		if (program == 0) {
			throw new RuntimeException(glGetProgramInfoLog(program));
		}

		compile(vertexShaderSource, GL_VERTEX_SHADER);
		compile(fragmentShaderSource, GL_FRAGMENT_SHADER);

		bindAttributes(bindingInfo);

		link();

		bindUniforms();
	}

	private void compile(String source, int type) {
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
		bindingInfo.getAttributePointers().forEach(ap -> glBindAttribLocation(program, ap.getLocation(), ap.getAttributeType().getName()));
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

	public void setUniform(String name, float value) {
		Uniform uniform = uniformMap.get(name);

		glUniform1f(uniform.getLocation(), value);
	}

	public void setUniform(String name, FloatBuffer floatBuffer) {
		Uniform uniform = uniformMap.get(name);

		glUniform1fv(uniform.getLocation(), floatBuffer);
	}

	public void setUniform(String name, int value) {
		Uniform uniform = uniformMap.get(name);

		glUniform1i(uniform.getLocation(), value);
	}

	public void setUniform(String name, Matrix4 value) {
		Uniform uniform = uniformMap.get(name);

		glUniformMatrix4fv(uniform.getLocation(), false, value.toBuffer());
	}

	public void setUniform(String name, Vector3 value) {
		Uniform uniform = uniformMap.get(name);

		glUniform3f(uniform.getLocation(), value.x, value.y, value.z);
	}

	public void setUniform(String name, Vector2 value) {
		Uniform uniform = uniformMap.get(name);

		glUniform2f(uniform.getLocation(), value.x, value.y);
	}

	public void setUniform(String name, Color value) {
		Uniform uniform = uniformMap.get(name);

		glUniform4f(uniform.getLocation(), value.r, value.g, value.b, value.a);
	}

	public void setUniform(String name, Matrix3 value) {
		Uniform uniform = uniformMap.get(name);

		glUniformMatrix3fv(uniform.getLocation(), false, value.toBuffer());
	}

	public void setUniform(String name, boolean value) {
		Uniform uniform = uniformMap.get(name);

		glUniform1i(uniform.getLocation(), value ? 1 : 0);
	}

	public void updateUniforms(RenderState renderState) {
		Matrix4 viewProjection = renderState.getViewProjection();
		Renderer renderer = renderState.getRenderer();
		Matrix4 model = renderer.getTransform().getLocalToWorldMatrix();
		Material material = renderer.getMaterial();
		Light light = renderState.getLight();
		Color ambientIntensity = renderState.getAmbientIntenstiy();
		Matrix4 mvp = viewProjection.mul(model);

		if (enabledLights) {
			setUniform("material.shininess", material.getShininess());
			setUniform("material.specularIntensity", material.getSpecularIntensity());

			if (light == null) {
				setUniform("lightType", 0);

				setUniform("ambientIntensity", ambientIntensity);
			} else {
				if (light instanceof DirectionalLight) {
					DirectionalLight directionalLight = (DirectionalLight) light;

					setUniform("lightType", 1);

					setUniform("light.color", directionalLight.getColor());
					setUniform("light.intensity", directionalLight.getIntensity());
					setUniform("light.direction", directionalLight.getTransform().forward());
				}

				if (receiveShadows && light.getShadows() == Light.Shadows.HARD) {
					setUniform("shadowMap", 2);
					renderState.getShadowMap().bind(2);

					setUniform("modelLightViewProjection", renderState.getLightViewProjection().mul(model));
					setUniform("minShadowVariance", light.getMinVariance());
					setUniform("lightBleed", light.getLightBleed());
				}
			}
		}

		setUniform("MVP", mvp);
		setUniform("M", model);

		if (material != null) {
			int i = 0;

			for (Material.TextureMap map : material.getTextures()) {
				setUniform(map.getName(), i);
				map.getTexture().bind(i);

				i++;
			}
		}
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

	public BindingInfo getBindingInfo() {
		return bindingInfo;
	}

	public boolean castsShadows() {
		return castShadows;
	}

	public boolean receivesShadows() {
		return receiveShadows;
	}

	public static class Builder {

		private static final String version = "330";
		private static String lightsTemplate = Resources.loadText("shaders/lights.template", Shader.class.getClassLoader());

		private List<AttributePointer> attributePointers;
		private List<String> uniformNames;
		private String vertexSource, fragmentSource;
		private StringBuilder vertexShader, fragmentShader;

		private boolean enabledTransformation;
		private boolean enabledLights;
		private boolean castShadows, recieveShadows;

		public Builder() {
			attributePointers = new ArrayList<>();
			uniformNames = new ArrayList<>();
		}

		public Builder setVertexShaderSource(String vs) {
			vertexSource = vs;

			return this;
		}

		public Builder setFragmentShaderSource(String fs) {
			fragmentSource = fs;

			return this;
		}

		public Shader build() {
			if (vertexSource == null) {
				throw new RuntimeException("vertex shader source is null");
			}

			if (fragmentSource == null) {
				throw new RuntimeException("fragment shader source is null");
			}

			vertexShader = new StringBuilder();
			fragmentShader = new StringBuilder();

			vertexShader.append("#version " + version + "\n");
			fragmentShader.append("#version " + version + "\n");

			preprocessVertexShader(vertexSource);
			preprocessFragmentShader(fragmentSource);

			return new Shader(enabledLights, castShadows, recieveShadows, vertexShader.toString(), fragmentShader.toString(), new BindingInfo(attributePointers), uniformNames);
		}

		private void preprocessVertexShader(String source) {
			int attributeLocation = 0;

			for (String line : source.split("\n")) {
				line = line.trim();

				if (line.startsWith("#attribute")) {
					String name = line.substring("#attribute".length()).trim();
					AttributeType type = AttributeType.POSITION;

					if (name.equals("position")) {
						type = AttributeType.POSITION;
					} else if (name.equals("uv")) {
						type = AttributeType.UV;
					} else if (name.equals("normal")) {
						type = AttributeType.NORMAL;
					} else if (name.equals("color")) {
						type = AttributeType.COLOR;
					}

					attributePointers.add(new AttributePointer(type, attributeLocation));
					vertexShader.append(String.format("\nin layout(location = %d) vec%d %s;", attributeLocation++, type.getSize(), type.getName()));
					vertexShader.append(String.format("\nout vec%d v%s;", type.getSize(), type.getName()));
				} else if (line.startsWith("#enable")) {
					String what = line.substring("#enable".length()).trim();

					if (what.equals("transformation")) {
						enabledTransformation = true;
						uniformNames.add("MVP");
						uniformNames.add("M");
					} else if (what.equals("lights")) {
						enabledLights = true;
					} else if (what.equals("shadowCast")) {
						castShadows = true;
					} else if (what.equals("shadowReceive")) {
						recieveShadows = true;
					}
				} else if (line.startsWith("uniform")) {
					String name = line.substring(line.lastIndexOf(' ') + 1, line.lastIndexOf(';')).trim();

					uniformNames.add(name);

					vertexShader.append(line);
				}
			}

			createVertexShaderMain();
		}

		private void createVertexShaderMain() {
			if (enabledTransformation) {
				vertexShader.append("\nuniform mat4 MVP;\nuniform mat4 M;");
			}

			if (enabledLights) {
				vertexShader.append("\nout vec4 vShadowMapCoords;\nuniform mat4 modelLightViewProjection;");
				uniformNames.add("modelLightViewProjection");
			}

			vertexShader.append("\nvoid main(){");

			attributePointers.forEach(ap -> {
				String name = ap.getAttributeType().getName();

				vertexShader.append("\nv");

				vertexShader.append(name);
				vertexShader.append(" = ");

				if (ap.getAttributeType() == AttributeType.POSITION) {
					vertexShader.append("M * " + name);
				} else if (ap.getAttributeType() == AttributeType.NORMAL) {
					vertexShader.append("(M * vec4(" + name + ", 0.0)).xyz");
				} else {
					vertexShader.append(name);
				}

				vertexShader.append(";");
			});

			if (enabledLights) {
				vertexShader.append("\nvShadowMapCoords = modelLightViewProjection * " + AttributeType.POSITION.getName() + ";");
			}

			if (enabledTransformation) {
				vertexShader.append("\ngl_Position = MVP * " + AttributeType.POSITION.getName() + ";");
			}

			vertexShader.append("\n}");
		}

		private void preprocessFragmentShader(String source) {
			if (enabledLights) {
				fragmentShader.append(lightsTemplate);

				uniformNames.add("shadowMap");
				uniformNames.add("modelLightViewProjection");
				uniformNames.add("minShadowVariance");
				uniformNames.add("lightBleed");
				uniformNames.add("cookie");
				uniformNames.add("cookieScale");
				uniformNames.add("ambientIntensity");

				uniformNames.add("light.direction");
				uniformNames.add("light.color");
				uniformNames.add("light.intensity");
				uniformNames.add("light.position");
				uniformNames.add("light.cutoff");
				uniformNames.add("light.range");

				uniformNames.add("material.shininess");
				uniformNames.add("material.specularIntensity");

				uniformNames.add("lightType");
			}

			attributePointers.forEach(ap -> {
				AttributeType type = ap.getAttributeType();

				fragmentShader.append(String.format("\nin vec%d v%s;", type.getSize(), type.getName()));
			});

			fragmentShader.append("\nin vec4 vShadowMapCoords;");

			for (String line : source.split("\n")) {
				line = line.trim();

				if (line.startsWith("uniform")) {
					String name = line.substring("uniform".length()).trim();

					uniformNames.add(name);
				} else if (line.startsWith("#texture")) {
					String name = line.substring("#texture".length()).trim();

					line = "\nuniform sampler2D " + name + ";";
					uniformNames.add(name);
				} else if (line.startsWith("#shader")) {
					line = line.substring("#shader".length());
					fragmentShader.append("\nvec4 customShader()");
				}

				fragmentShader.append(line);
			}

			fragmentShader.append("\nvoid main(){gl_FragColor = customShader()");

			if (enabledLights) {
				fragmentShader.append(String.format("*calculateLightFactor(v%s, v%s)", AttributeType.POSITION.getName(), AttributeType.NORMAL.getName()));
				fragmentShader.append("*ambientIntensity");
			}

			if (recieveShadows) {
				fragmentShader.append(String.format("*calculateShadowFactor(vShadowMapCoords)"));
			}

			fragmentShader.append(";}");
		}

	}

}
