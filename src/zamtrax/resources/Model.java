package zamtrax.resources;

import zamtrax.Vertex;

import java.util.List;

public final class Model {

	private List<Vertex> vertices;
	private List<Integer> indices;

	public Model(List<Vertex> vertices, List<Integer> indices) {
		this.vertices = vertices;
		this.indices = indices;
	}

	public List<Vertex> getVertices() {
		return vertices;
	}

	public List<Integer> getIndices() {
		return indices;
	}

}
