package zamtrax.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

final class ShaderResource extends ReferencedResource {

	private static Map<Integer, ShaderResource> resourceMap = new HashMap<>();

	private final int id;
	private final int program;
	private BindingInfo bindingInfo;
	private Map<CharSequence, Uniform> uniforms;

	private ShaderResource(int id) {
		this.id = id;
		program = glCreateProgram();

		if (program == 0) {
			throw new Error(glGetProgramInfoLog(program));
		}

		uniforms = new HashMap<>();
	}

	public int getProgram() {
		return program;
	}

	public BindingInfo getBindingInfo() {
		return bindingInfo;
	}

	public Map<CharSequence, Uniform> getUniforms() {
		return uniforms;
	}

	public void setBindingInfo(BindingInfo bindingInfo) {
		this.bindingInfo = bindingInfo;
	}

	public void setUniforms(List<Uniform> uniforms) {
		uniforms.forEach(u -> this.uniforms.put(u.getName(), u));
	}

	@Override
	public void dispose() {
		glDeleteProgram(program);
		resourceMap.remove(id);
	}

	public static ShaderResource create(int id) {
		ShaderResource resource = resourceMap.get(id);

		if (resource == null) {
			resource = new ShaderResource(id);

			resourceMap.put(id, resource);
		} else {
			resource.addReference();
		}

		return resource;
	}

}

