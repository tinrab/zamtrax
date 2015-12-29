package zamtrax;

import javax.vecmath.Vector3f;

public class Vector3 {

	public static final Vector3 ZERO = new Vector3(0.0f, 0.0f, 0.0f);
	public static final Vector3 LEFT = new Vector3(-1.0f, 0.0f, 0.0f);
	public static final Vector3 RIGHT = new Vector3(1.0f, 0.0f, 0.0f);
	public static final Vector3 UP = new Vector3(0.0f, 1.0f, 0.0f);
	public static final Vector3 DOWN = new Vector3(0.0f, -1.0f, 0.0f);
	public static final Vector3 FORWARD = new Vector3(0.0f, 0.0f, 1.0f);
	public static final Vector3 BACK = new Vector3(0.0f, 0.0f, -1.0f);

	public float x, y, z;

	public Vector3() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vector2 v) {
		x = v.x;
		y = v.y;
		z = 0.0f;
	}

	public Vector3(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3(Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public void set(javax.vecmath.Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3 set(Vector2 v) {
		return set(v.x, v.y);
	}

	public Vector3 set(float x, float y) {
		this.x = x;
		this.y = y;

		return this;
	}

	public Vector3 set(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;

		return this;
	}

	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public float length() {
		return Mathf.sqrt(x * x + y * y + z * z);
	}

	public float squaredLength() {
		return x * x + y * y + z * z;
	}

	public Vector3 normalized() {
		float len = length();

		if (len == 0.0f) {
			return new Vector3();
		}

		return new Vector3(x / len, y / len, z / len);
	}

	public float dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3 add(Vector3 v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3 add(float x, float y, float z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 sub(Vector3 v) {
		return sub(v.x, v.y, v.z);
	}

	public Vector3 sub(float x, float y, float z) {
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}

	public Vector3 mul(float a) {
		return new Vector3(x * a, y * a, z * a);
	}

	public Vector3 mul(Vector3 v) {
		return mul(v.x, v.y, v.z);
	}

	public Vector3 mul(float x, float y, float z) {
		return new Vector3(this.x * x, this.y * y, this.z * z);
	}

	public Vector3 div(float a) {
		return new Vector3(x / a, y / a, z / a);
	}

	public Vector3 div(Vector3 v) {
		return div(v.x, v.y, v.z);
	}

	public Vector3 div(float x, float y, float z) {
		return new Vector3(this.x / x, this.y / y, this.z / z);
	}

	public Vector3 rotate(Vector3 axis, float angle) {
		float sin = Mathf.sin(-angle);
		float cos = Mathf.cos(-angle);

		return cross(this, axis.mul(sin)).add((mul(cos)).add(axis.mul(dot(axis.mul(1.0f - cos)))));
	}

	public Vector3 rotate(Quaternion q) {
		Quaternion conj = q.conjugate();
		Quaternion w = q.mul(this).mul(conj);

		return new Vector3(w.x, w.y, w.z);
	}

	public static Vector3 cross(Vector3 left, Vector3 right) {
		return new Vector3(left.y * right.z - right.y * left.z,
				left.z * right.x - right.z * left.x,
				left.x * right.y - right.x * left.y);
	}

	public static Vector3 lerp(Vector3 a, Vector3 b, float t) {
		return new Vector3(a.x + (b.x - a.x) / t, a.y + (b.y - a.y) / t, a.z + (b.z - a.z) / t);
	}

	public static float distance(Vector3 a, Vector3 b) {
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		float dz = b.z - a.z;

		return Mathf.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public float[] toArray() {
		return new float[]{x, y, z};
	}

	@Override
	public String toString() {
		return String.format("Vector3(%f, %f, %f)", x, y, z);
	}

	public javax.vecmath.Vector3f toVecmath() {
		return new javax.vecmath.Vector3f(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		Vector3 other = (Vector3) obj;

		return x == other.y && y == other.y && z == other.z;
	}

}
