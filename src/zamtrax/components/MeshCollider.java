package zamtrax.components;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import zamtrax.Collider;
import zamtrax.RequireComponent;
import zamtrax.Vector3;
import zamtrax.Vertex;
import zamtrax.resources.Mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

@RequireComponent(components = {MeshFilter.class})
public class MeshCollider extends Collider {

	@Override
	public CollisionShape getCollisionShape() {
		Mesh mesh = getComponent(MeshFilter.class).getMesh();
		List<Integer> indexList = mesh.getIndices();
		List<Vertex> vertexList = mesh.getVertices();

		int[] indices = new int[indexList.size()];
		float[] coords = new float[vertexList.size() * 3];

		for (int i = 0; i < indices.length; i++) {
			indices[i] = indexList.get(i);
		}

		int i = 0;
		for (Vertex vertex : vertexList) {
			Vector3 pos = vertex.getPosition();

			coords[i++] = pos.x;
			coords[i++] = pos.y;
			coords[i++] = pos.z;
		}

		int numTriangles = indices.length / 3;
		ByteBuffer triangleIndexBase = ByteBuffer.allocateDirect(indices.length * Integer.BYTES).order(ByteOrder.nativeOrder());
		triangleIndexBase.asIntBuffer().put(indices);
		int triangleIndexStride = 3 * 4;
		int numVertices = coords.length / 3;
		ByteBuffer vertexBase = ByteBuffer.allocateDirect(coords.length * Float.BYTES).order(ByteOrder.nativeOrder());
		vertexBase.asFloatBuffer().put(coords);
		int vertexStride = 3 * 4;

		TriangleIndexVertexArray triangleIndexVertexArray = new TriangleIndexVertexArray(numTriangles, triangleIndexBase, triangleIndexStride, numVertices, vertexBase, vertexStride);

		return new BvhTriangleMeshShape(triangleIndexVertexArray, true);
	}

}
