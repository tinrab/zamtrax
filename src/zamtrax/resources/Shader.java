package zamtrax.resources;

import javafx.scene.effect.*;
import zamtrax.*;
import zamtrax.components.*;
import zamtrax.components.Light;
import zamtrax.rendering.RenderState;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.StringReader;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

	private int pass;
	private int program;
	private Map<String, Uniform> uniformMap;
	private BindingInfo bindingInfo;
	private boolean enabledLights, enableAmbient;
	private boolean castShadows, receiveShadows;

	public Shader(int pass, boolean enabledLights, boolean enableAmbient, boolean castShadows, boolean receiveShadows, String vertexShaderSource, String fragmentShaderSource, BindingInfo bindingInfo, List<String> uniformNames) {
		this.pass = pass;
		this.enabledLights = enabledLights;
		this.enableAmbient = enableAmbient;
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

	public Shader(String vertexShaderSource, String fragmentShaderSource) {
		uniformMap = new HashMap<>();

		program = glCreateProgram();

		if (program == 0) {
			throw new RuntimeException(glGetProgramInfoLog(program));
		}

		compile(vertexShaderSource, GL_VERTEX_SHADER);
		compile(fragmentShaderSource, GL_FRAGMENT_SHADER);
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

	public void bindAttribute(int location, String name) {
		glBindAttribLocation(program, location, name);
	}

	public void bindUniform(String name) {
		int location = glGetUniformLocation(program, name);
		Uniform uniform = new Uniform(name);

		uniform.setLocation(location);

		uniformMap.put(name, uniform);
	}

	public void link() {
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

		setUniform("MVP", mvp);
		setUniform("M", model);
		setUniform("P", viewProjection);

		if (enableAmbient) {
			setUniform("ambientIntensity", ambientIntensity == null ? Color.createWhite() : ambientIntensity);
		}

		if (enabledLights) {
			setUniform("material.shininess", material.getShininess());
			setUniform("material.specularIntensity", material.getSpecularIntensity());
			setUniform("useCookie", false);

			if (light != null) {
				if (light instanceof DirectionalLight) {
					DirectionalLight directionalLight = (DirectionalLight) light;

					setUniform("lightType", 1);

					setUniform("light.color", directionalLight.getColor());
					setUniform("light.intensity", directionalLight.getIntensity());
					setUniform("light.direction", directionalLight.getTransform().forward());
				} else if (light instanceof PointLight) {
					PointLight pointLight = (PointLight) light;

					setUniform("lightType", 2);

					setUniform("light.color", pointLight.getColor());
					setUniform("light.intensity", pointLight.getIntensity());
					setUniform("light.range", pointLight.getRange());
					setUniform("light.position", pointLight.getTransform().getPosition());
				} else if (light instanceof SpotLight) {
					SpotLight spotLight = (SpotLight) light;

					setUniform("lightType", 3);

					setUniform("light.color", spotLight.getColor());
					setUniform("light.intensity", spotLight.getIntensity());
					setUniform("light.range", spotLight.getRange());
					setUniform("light.position", spotLight.getTransform().getPosition());
					setUniform("light.cutoff", spotLight.getCutoff());
					setUniform("light.direction", spotLight.getTransform().forward());
				}

				if (receiveShadows && light.getShadows() == Light.Shadows.HARD) {
					setUniform("shadowMap", 2);
					renderState.getShadowMap().bind(2);

					setUniform("modelLightViewProjection", renderState.getLightViewProjection().mul(model));
					setUniform("minShadowVariance", light.getMinVariance());
					setUniform("lightBleed", light.getLightBleed());

					if (light.getCookie() != null) {
						setUniform("cookie", 3);
						setUniform("useCookie", true);
						setUniform("cookieScale", light.getCookieScale());

						light.getCookie().bind(3);
					} else {
						setUniform("useCookie", false);
					}
				}
			} else {
				//setUniform("ambientIntensity", Color.createWhite());
				setUniform("lightType", 0);
			}
		} else {
			setUniform("lightType", 0);
		}

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

	public boolean lightsEnabled() {
		return enabledLights;
	}

	public boolean ambientEnabled() {
		return enableAmbient;
	}

	public int getPass() {
		return pass;
	}

	public static class Builder {

		private static final String version = "330";
		private static String lightsTemplate = Resources.loadText("shaders/lights.template", Shader.class.getClassLoader());

		private List<AttributePointer> attributePointers;
		private List<String> uniformNames;
		private String vertexSource, fragmentSource;
		private StringBuilder vertexShader, fragmentShader;
		private int pass;
		private boolean enabledLights, enableAmbient;
		private boolean castShadows, recieveShadows;
		private String customVertexShader;

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

			return new Shader(pass, enabledLights, enableAmbient, castShadows, recieveShadows, vertexShader.toString(), fragmentShader.toString(), new BindingInfo(attributePointers), uniformNames);
		}

		private void preprocessVertexShader(String source) {
			try {
				int attributeLocation = 0;

				uniformNames.add("MVP");
				uniformNames.add("M");
				uniformNames.add("P");

				BufferedReader br = new BufferedReader(new StringReader(source));
				String line;

				while ((line = br.readLine()) != null) {
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

						if (what.equals("lights")) {
							enabledLights = true;
							enableAmbient = true;
						} else if (what.equals("ambient")) {
							enableAmbient = true;
						} else if (what.equals("shadowCast")) {
							castShadows = true;
						} else if (what.equals("shadowReceive")) {
							recieveShadows = true;
						}
					} else if (line.startsWith("uniform")) {
						String name = line.substring(line.lastIndexOf(' ') + 1, line.lastIndexOf(';')).trim();

						uniformNames.add(name);

						vertexShader.append(line);
					} else if (line.startsWith("#pass")) {
						pass = Integer.parseInt(line.substring("#pass".length()).trim());
					} else if (line.startsWith("#shader")) {
						customVertexShader = line.substring("#shader".length());

						while ((line = br.readLine()) != null) {
							customVertexShader += line;
						}
					}
				}

				createVertexShaderMain();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		private void createVertexShaderMain() {
			vertexShader.append("\nuniform mat4 MVP;\nuniform mat4 M;\nuniform mat4 P;");

			if (enabledLights) {

			}

			if (recieveShadows) {
				vertexShader.append("\nout vec4 vShadowMapCoords;\nuniform mat4 modelLightViewProjection;");
				uniformNames.add("modelLightViewProjection");
			}

			if (customVertexShader != null) {
				vertexShader.append("vec4 customShader()" + customVertexShader);
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

			if (recieveShadows) {
				vertexShader.append("\nvShadowMapCoords = modelLightViewProjection * " + AttributeType.POSITION.getName() + ";");
			}

			if (customVertexShader != null) {
				vertexShader.append("\ngl_Position = customShader();");
			} else {
				vertexShader.append("\ngl_Position = MVP * " + AttributeType.POSITION.getName() + ";");
			}

			vertexShader.append("\n}");
		}

		private void preprocessFragmentShader(String source) {
			fragmentShader.append("\nuniform int lightType;");
			uniformNames.add("lightType");

			if (enabledLights || recieveShadows) {
				fragmentShader.append(lightsTemplate);
			}

			if (enableAmbient) {
				uniformNames.add("ambientIntensity");

				fragmentShader.append("\nuniform vec4 ambientIntensity;");
			}

			if (enabledLights) {
				uniformNames.add("cookie");
				uniformNames.add("cookieScale");

				uniformNames.add("light.direction");
				uniformNames.add("light.color");
				uniformNames.add("light.intensity");
				uniformNames.add("light.position");
				uniformNames.add("light.cutoff");
				uniformNames.add("light.range");

				uniformNames.add("material.shininess");
				uniformNames.add("material.specularIntensity");

				uniformNames.add("cookie");
				uniformNames.add("cookieScale");
				uniformNames.add("useCookie");
			}

			if (recieveShadows) {
				uniformNames.add("shadowMap");
				uniformNames.add("modelLightViewProjection");
				uniformNames.add("minShadowVariance");
				uniformNames.add("lightBleed");
				fragmentShader.append("\nin vec4 vShadowMapCoords;");
			}

			attributePointers.forEach(ap -> {
				AttributeType type = ap.getAttributeType();

				fragmentShader.append(String.format("\nin vec%d v%s;", type.getSize(), type.getName()));
			});

			for (String line : source.split("\n")) {
				line = line.trim();

				if (line.startsWith("uniform")) {
					String name = line.substring(line.lastIndexOf(' ') + 1, line.lastIndexOf(';')).trim();

					uniformNames.add(name);
				} else if (line.startsWith("#texture")) {
					String name = line.substring("#texture".length()).trim();

					line = "\nuniform sampler2D " + name + ";";
					uniformNames.add(name);
				} else if (line.startsWith("#shader")) {
					line = line.substring("#shader".length());
					fragmentShader.append("\nvec4 customShader()");
				}

				fragmentShader.append("\n" + line);
			}

			fragmentShader.append("\nvoid main(){");

			if(recieveShadows) {
				fragmentShader.append("\nvec4 cookieColor = vec4(1.0);");
				fragmentShader.append("if(useCookie){cookieColor = calculateCookie(cookie,vShadowMapCoords,cookieScale); }");
			}

			fragmentShader.append("gl_FragColor = customShader()");

			if (enabledLights) {
				fragmentShader.append(String.format("*calculateLightFactor(v%s, v%s)", AttributeType.POSITION.getName(), AttributeType.NORMAL.getName()));
			}

			if (enableAmbient) {
				fragmentShader.append("*ambientIntensity");
			}

			if (recieveShadows) {
				fragmentShader.append("*cookieColor*calculateShadowFactor(vShadowMapCoords)");
			}

			fragmentShader.append(";}");
		}

	}

}
