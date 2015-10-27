package zamtrax;

public class Vector2 {

	public static final Vector2 ZERO = new Vector2();
	public static final Vector2 LEFT = new Vector2(-1.0f, 0.0f);
	public static final Vector2 RIGHT = new Vector2(1.0f, 0.0f);
	public static final Vector2 UP = new Vector2(0.0f, 1.0f);
	public static final Vector2 DOWN = new Vector2(0.0f, -1.0f);

	public float x, y;

	public Vector2() {
		x = 0.0f;
		y = 0.0f;
	}

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	public Vector2 set(float x, float y) {
		this.x = x;
		this.y = y;

		return this;
	}

	public float length() {
		return Mathf.sqrt(x * x + y * y);
	}

	public float squaredLength() {
		return x * x + y * y;
	}

	public Vector2 normalized() {
		float len = length();

		return new Vector2(x / len, y / len);
	}

	public float dot(Vector2 v) {
		return x * v.x + y * v.y;
	}

	public Vector2 add(Vector2 v) {
		return add(v.x, v.y);
	}

	public Vector2 add(float x, float y) {
		return new Vector2(this.x + x, this.y + y);
	}

	public Vector2 sub(Vector2 v) {
		return add(v.x, v.y);
	}

	public Vector2 sub(float x, float y) {
		return new Vector2(this.x - x, this.y - y);
	}

	public Vector2 mul(Vector2 v) {
		return mul(v.x, v.y);
	}

	public Vector2 mul(float a) {
		return new Vector2(x * a, y * a);
	}

	public Vector2 mul(float x, float y) {
		return new Vector2(this.x * x, this.y * y);
	}

	public Vector2 div(float a) {
		return new Vector2(x / a, y / a);
	}

	public Vector2 div(Vector2 v) {
		return div(v.x, v.y);
	}

	public Vector2 div(float x, float y) {
		return new Vector2(this.x / x, this.y / y);
	}

	public static Vector2 lerp(Vector2 a, Vector2 b, float t) {
		return new Vector2(a.x + (b.x - a.x) / t, a.y + (b.y - a.y) / t);
	}

	public float[] toArray() {
		return new float[]{x, y};
	}

	@Override
	public String toString() {
		return String.format("Vector2(%f, %f)", x, y);
	}

	@Override
	public boolean equals(Object obj) {
		Vector2 other = (Vector2) obj;

		return x == other.x && y == other.y;
	}

}
