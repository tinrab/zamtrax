package zamtrax;

import javax.vecmath.Quat4f;

public class Quaternion {

	public static final Quaternion IDENTITY = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

	public float x, y, z, w;

	public Quaternion() {
	}

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternion(Quaternion q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
	}

	public Quaternion(Quat4f q) {
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}

	public void set(javax.vecmath.Quat4f q) {
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}

	public Quaternion(Matrix4 m) {
		float trace = m.get(0, 0) + m.get(1, 1) + m.get(2, 2);

		if (trace > 0) {
			float s = 0.5f / Mathf.sqrt(trace + 1.0f);
			w = 0.25f / s;
			x = (m.get(1, 2) - m.get(2, 1)) * s;
			y = (m.get(2, 0) - m.get(0, 2)) * s;
			z = (m.get(0, 1) - m.get(1, 0)) * s;
		} else {
			if (m.get(0, 0) > m.get(1, 1) && m.get(0, 0) > m.get(2, 2)) {
				float s = 2.0f * Mathf.sqrt(1.0f + m.get(0, 0) - m.get(1, 1) - m.get(2, 2));
				w = (m.get(1, 2) - m.get(2, 1)) / s;
				x = 0.25f * s;
				y = (m.get(1, 0) + m.get(0, 1)) / s;
				z = (m.get(2, 0) + m.get(0, 2)) / s;
			} else if (m.get(1, 1) > m.get(2, 2)) {
				float s = 2.0f * Mathf.sqrt(1.0f + m.get(1, 1) - m.get(0, 0) - m.get(2, 2));
				w = (m.get(2, 0) - m.get(0, 2)) / s;
				x = (m.get(1, 0) + m.get(0, 1)) / s;
				y = 0.25f * s;
				z = (m.get(2, 1) + m.get(1, 2)) / s;
			} else {
				float s = 2.0f * Mathf.sqrt(1.0f + m.get(2, 2) - m.get(0, 0) - m.get(1, 1));
				w = (m.get(0, 1) - m.get(1, 0)) / s;
				x = (m.get(2, 0) + m.get(0, 2)) / s;
				y = (m.get(1, 2) + m.get(2, 1)) / s;
				z = 0.25f * s;
			}
		}

		float len = length();

		x /= len;
		y /= len;
		z /= len;
		w /= len;
	}

	public Quaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;

		return this;
	}

	public Quaternion set(Quaternion q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;

		return this;
	}

	public Quaternion loadIdentity() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
		w = 1.0f;

		return this;
	}

	public Quaternion normalized() {
		float len = length();

		return new Quaternion(x / len, y / len, z / len, w / len);
	}

	public float length() {
		return Mathf.sqrt(x * x + y * y + z * z + w * w);
	}

	public Quaternion mul(float a) {
		return new Quaternion(x * a, y * a, z * a, w * a);
	}

	public Vector3 mul(Vector3 v, Vector3 result) {
		Vector3 qv = new Vector3(x, y, z);

		Vector3 uv = Vector3.cross(qv, v);
		Vector3 uuv = Vector3.cross(qv, uv);

		uv.mul(w * 2.0f);
		uuv.mul(2.0f);

		result.set(v).add(uv).add(uuv);

		return result;
	}

	public Quaternion mul(Quaternion q) {
		float nx = w * q.x + x * q.w + y * q.z - z * q.y;
		float ny = w * q.y + y * q.w + z * q.x - x * q.z;
		float nz = w * q.z + z * q.w + x * q.y - y * q.x;
		float nw = w * q.w - x * q.x - y * q.y - z * q.z;

		return new Quaternion(nx, ny, nz, nw);
	}

	public Quaternion mul(Vector3 v) {
		float nw = -x * v.x - y * v.y - z * v.z;
		float nx = w * v.x + y * v.z - z * v.y;
		float ny = w * v.y + z * v.x - x * v.z;
		float nz = w * v.z + x * v.y - y * v.x;

		return new Quaternion(nx, ny, nz, nw);
	}

	public Quaternion add(Quaternion q) {
		return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.z);
	}

	public Quaternion sub(Quaternion q) {
		return new Quaternion(x - q.x, y - q.y, z - q.z, w - q.w);
	}

	public Quaternion conjugate() {
		return new Quaternion(-x, -y, -z, w);
	}

	public Quaternion inverse() {
		return normalized().conjugate();
	}

	public float dot(Quaternion q) {
		return x * q.x + y * q.y + z * q.z + w * q.w;
	}

	public Vector3 rotatePoint(Vector3 v) {
		return v.rotate(this);
	}

	public Matrix4 toMatrix() {
		Vector3 forward = new Vector3(2.0f * (x * z - w * y), 2.0f * (y * z + w * x), 1.0f - 2.0f * (x * x + y * y));
		Vector3 up = new Vector3(2.0f * (x * y + w * z), 1.0f - 2.0f * (x * x + z * z), 2.0f * (y * z - w * x));
		Vector3 right = new Vector3(1.0f - 2.0f * (y * y + z * z), 2.0f * (x * y - w * z), 2.0f * (x * z + w * y));

		Matrix4 m = new Matrix4();

		return m.loadRotation(forward, up, right);
	}

	public Vector3 getEulerAngles() {
		Vector3 euler = new Vector3();

		euler.x = Mathf.atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y));
		euler.y = Mathf.asin(2 * (w * y - z * x));
		euler.z = Mathf.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));

		return euler;
	}

	public javax.vecmath.Quat4f toVecmath() {
		return new javax.vecmath.Quat4f(x, y, z, w);
	}

	public static Quaternion slerp(Quaternion a, Quaternion b, float t) {
		float cosAngle = a.dot(b);
		final boolean allowFlip = true;

		float c1, c2;

		if ((1.0 - Mathf.abs(cosAngle)) < 0.01) {
			c1 = 1.0f - t;
			c2 = t;
		} else {
			float angle = Mathf.acos(Mathf.abs(cosAngle));
			float sinAngle = Mathf.sin(angle);
			c1 = Mathf.sin(angle * (1.0f - t)) / sinAngle;
			c2 = Mathf.sin(angle * t) / sinAngle;
		}

		if (allowFlip && (cosAngle < 0.0))
			c1 = -c1;

		return new Quaternion(c1 * a.x + c2 * b.x, c1 * a.y + c2 * b.y, c1 * a.z + c2 * b.z, c1 * a.w + c2 * b.w);
	}

	public static Quaternion createIdentity() {
		return new Quaternion().loadIdentity();
	}

	public static Quaternion fromAxisAngle(Vector3 axis, float angle) {
		float a = (angle * Mathf.DEG_TO_RAD) / 2.0f;
		float s = Mathf.fastSin(a);

		return new Quaternion(axis.x * s, axis.y * s, axis.z * s, Mathf.fastCos(a));
	}

	public static Quaternion fromEuler(Vector3 eulerAngles) {
		return fromEuler(eulerAngles.x, eulerAngles.y, eulerAngles.z);
	}

	public static Quaternion fromEuler(float x, float y, float z) {
		Quaternion rx = fromAxisAngle(Vector3.RIGHT, x);
		Quaternion ry = fromAxisAngle(Vector3.UP, y);
		Quaternion rz = fromAxisAngle(Vector3.BACK, z);

		return rz.mul(ry.mul(rx));
	}

	public static Quaternion fromLookAt(Vector3 direction, Vector3 up) {
		Vector3 f = direction.normalized();
		Vector3 r = Vector3.cross(f, up).normalized();
		Vector3 u = Vector3.cross(r, f);

		return new Quaternion(Matrix4.createRotation(f, up));
	}

}
