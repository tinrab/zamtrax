package zamtrax.resources;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class MeshResource extends ReferencedResource {

	private static Map<Integer, MeshResource> resourceMap = new HashMap<>();

	private final int id;
	private int vaoId, vboId, iboId;

	private MeshResource(int id) {
		this.id = id;
		vaoId = glGenVertexArrays();
		vboId = glGenBuffers();
		iboId = glGenBuffers();
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVboId() {
		return vboId;
	}

	public int getIboId() {
		return iboId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof MeshResource)) {
			return false;
		}

		return this == obj || ((MeshResource) obj).vboId == vboId;
	}

	@Override
	public void dispose() {
		glDeleteVertexArrays(vaoId);
		glDeleteBuffers(vboId);
		glDeleteBuffers(iboId);
		resourceMap.remove(id);
	}

	public static MeshResource create(int id) {
		MeshResource resource = resourceMap.get(id);

		if (resource == null) {
			resource = new MeshResource(id);

			resourceMap.put(id, resource);
		} else {
			resource.addReference();
		}

		return resource;
	}

}
