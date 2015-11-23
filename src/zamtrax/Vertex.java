package zamtrax;

public final class Vertex {

	public Vector3 position;
	public Color color;
	public Vector3 normal;
	public Vector2 uv;

	public Vertex() {
	}

	public Vertex(Vector3 position) {
		this.position = new Vector3(position);
	}

	public Vertex(float x, float y, float z) {
		position = new Vector3(x, y, z);
	}

	public Vertex(float x, float y, float z, Color color) {
		position = new Vector3(x, y, z);
		this.color = new Color(color);
	}

	public Vertex(float x, float y, float z, float r, float g, float b) {
		position = new Vector3(x, y, z);
		color = new Color(r, g, b);
	}

	public Vertex(float x, float y, float z, float u, float v) {
		this.position = new Vector3(x, y, z);
		this.uv = new Vector2(u, v);
	}

	public int getSize() {
		int size = 4;

		if (color != null) {
			size += 4;
		}

		if (normal != null) {
			size += 3;
		}

		if (uv != null) {
			size += 2;
		}

		return size;
	}

	public static Vertex[] fromArray(float[] vertices, float[] colors) {
		Vertex[] v = new Vertex[vertices.length / 3];

		for (int i = 0; i < vertices.length; i += 3) {
			v[i / 3] = new Vertex(vertices[i], vertices[i + 1], vertices[i + 2], colors[i], colors[i + 1], colors[i + 2]);
		}

		return v;
	}

}
