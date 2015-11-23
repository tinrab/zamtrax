package zamtrax.resources;

import org.lwjgl.BufferUtils;
import zamtrax.Vector3;
import zamtrax.Vertex;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

final class IndexedMesh implements Mesh {

	private MeshResource resource;
	private List<Vertex> vertices;
	private List<Integer> indices;
	private BindingInfo bindingInfo;
	private int drawType;

	IndexedMesh(int id, List<Vertex> vertices, List<Integer> indices, BindingInfo bindingInfo, boolean dynamic) {
		this.vertices = vertices;
		this.indices = indices;
		this.bindingInfo = bindingInfo;
		drawType = dynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW;

		resource = MeshResource.create(id);
	}

	@Override
	public void build() {
		IntBuffer ib = createIndexBuffer();
		FloatBuffer vb = createVertexBuffer();
		int vertexSize = bindingInfo.getSize();

		glBindVertexArray(resource.getVaoId());

		glBindBuffer(GL_ARRAY_BUFFER, resource.getVboId());
		glBufferData(GL_ARRAY_BUFFER, vb, drawType);

		int offset = 0, i = 0;

		for (AttributePointer ap : bindingInfo.getAttributePointers()) {
			glVertexAttribPointer(ap.getLocation(), ap.getAttributeType().getSize(), GL_FLOAT, false, vertexSize * 4, offset);

			offset += ap.getAttributeType().getSize() * 4;
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIboId());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, drawType);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private IntBuffer createIndexBuffer() {
		IntBuffer ib = BufferUtils.createIntBuffer(indices.size());

		indices.forEach(ib::put);

		ib.flip();

		return ib;
	}

	private FloatBuffer createVertexBuffer() {
		FloatBuffer vb = BufferUtils.createFloatBuffer(vertices.size() * bindingInfo.getSize());

		for (Vertex v : vertices) {
			bindingInfo.getAttributePointers().forEach(ap -> {
				switch (ap.getAttributeType()) {
					case POSITION:
						vb.put(v.position.toArray());
						break;
					case UV:
						vb.put(v.uv.toArray());
						break;
					case NORMAL:
						vb.put(v.normal.toArray());
						break;
					case COLOR:
						vb.put(v.color.toArray());
						break;
				}
			});
		}

		vb.flip();

		return vb;
	}

	@Override
	public void render() {
		glBindVertexArray(resource.getVaoId());
		bindingInfo.getAttributePointers().forEach(ap -> glEnableVertexAttribArray(ap.getLocation()));

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIboId());
		glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		bindingInfo.getAttributePointers().forEach(ap -> glDisableVertexAttribArray(ap.getLocation()));
		glBindVertexArray(0);
	}

	void calculateNormals() {
		for (Vertex v : vertices) {
			v.normal = new Vector3();
		}

		/*
		for (int i = 0; i < indices.size(); i += 3) {
			int i1 = indices.get(i + 0);
			int i2 = indices.get(i + 1);
			int i3 = indices.get(i + 2);

			Vector3 v1 = vertices.get(i1).position.sub(vertices.get(i2).position);
			Vector3 v2 = vertices.get(i3).position.sub(vertices.get(i2).position);

			Vector3 normal = Vector3.cross(v1, v2).normalized();

			vertices.get(i1).normal = vertices.get(i1).normal.add(normal);
			vertices.get(i2).normal = vertices.get(i2).normal.add(normal);
			vertices.get(i3).normal = vertices.get(i3).normal.add(normal);
		}
		*/
		for (int i = 0; i < indices.size(); i += 3) {
			int i1 = indices.get(i);
			int i2 = indices.get(i + 1);
			int i3 = indices.get(i + 2);

			Vector3 v1 = vertices.get(i2).position.sub(vertices.get(i1).position);
			Vector3 v2 = vertices.get(i3).position.sub(vertices.get(i1).position);

			Vector3 normal = Vector3.cross(v1, v2).normalized();

			vertices.get(i1).normal = vertices.get(i1).normal.add(normal).normalized();
			vertices.get(i2).normal = vertices.get(i2).normal.add(normal).normalized();
			vertices.get(i3).normal = vertices.get(i3).normal.add(normal).normalized();
			vertices.get(i1).normal = vertices.get(i1).normal.add(normal).normalized();
		}

		for (Vertex v : vertices) {
			v.normal = v.normal.normalized();
		}
	}

	@Override
	public void dispose() {
		resource.removeReference();
	}

}
