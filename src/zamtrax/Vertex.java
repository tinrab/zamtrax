package zamtrax;

public final class Vertex {

	public Vector3 position;
	public Color color;
	public Vector3 normal;
	public Vector2 uv;

	public Vertex() {
	}

	public Vertex(float x, float y, float z, float u, float v) {
		position = new Vector3(x, y, z);
		uv = new Vector2(u, v);
	}

	public Vertex(Vector3 position) {
		this.position = new Vector3(position);
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = new Color(color);
	}

	public Vector3 getNormal() {
		return normal;
	}

	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}

	public Vector2 getUV() {
		return uv;
	}

	public void setUV(Vector2 uv) {
		this.uv = uv;
	}

	public void setX(float x) {
		if (position == null) {
			position = new Vector3();
		}

		position.x = x;
	}

	public void setY(float y) {
		if (position == null) {
			position = new Vector3();
		}

		position.y = y;
	}

	public void setZ(float z) {
		if (position == null) {
			position = new Vector3();
		}

		position.z = z;
	}

	public void setU(float u) {
		if (uv == null) {
			uv = new Vector2();
		}

		uv.x = u;
	}

	public void setV(float v) {
		if (uv == null) {
			uv = new Vector2();
		}

		uv.y = v;
	}

	public void setRed(float r) {
		if (color == null) {
			color = new Color();
		}

		color.r = r;
	}

	public void setGreen(float g) {
		if (color == null) {
			color = new Color();
		}

		color.g = g;
	}

	public void setBlue(float b) {
		if (color == null) {
			color = new Color();
		}

		color.b = b;
	}

	public void setNX(float nx) {
		if (normal == null) {
			normal = new Vector3();
		}

		normal.x = nx;
	}

	public void setNY(float ny) {
		if (normal == null) {
			normal = new Vector3();
		}

		normal.y = ny;
	}

	public void setNZ(float nz) {
		if (normal == null) {
			normal = new Vector3();
		}

		normal.z = nz;
	}

	public int getSize() {
		int size = 0;

		if (position != null) {
			size += 4;
		}

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

}
