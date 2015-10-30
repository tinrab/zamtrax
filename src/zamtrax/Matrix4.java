package zamtrax;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Matrix4 {

	public float[][] elements;

	public Matrix4() {
		elements = new float[4][4];
	}

	public float get(int i, int j) {
		return elements[i][j];
	}

	public Matrix4 loadIdentity() {
		elements[0][0] = 1;
		elements[0][1] = 0;
		elements[0][2] = 0;
		elements[0][3] = 0;
		elements[1][0] = 0;
		elements[1][1] = 1;
		elements[1][2] = 0;
		elements[1][3] = 0;
		elements[2][0] = 0;
		elements[2][1] = 0;
		elements[2][2] = 1;
		elements[2][3] = 0;
		elements[3][0] = 0;
		elements[3][1] = 0;
		elements[3][2] = 0;
		elements[3][3] = 1;

		return this;
	}

	public Matrix4 loadTranslation(float x, float y, float z) {
		elements[0][0] = 1;
		elements[0][1] = 0;
		elements[0][2] = 0;
		elements[0][3] = x;
		elements[1][0] = 0;
		elements[1][1] = 1;
		elements[1][2] = 0;
		elements[1][3] = y;
		elements[2][0] = 0;
		elements[2][1] = 0;
		elements[2][2] = 1;
		elements[2][3] = z;
		elements[3][0] = 0;
		elements[3][1] = 0;
		elements[3][2] = 0;
		elements[3][3] = 1;

		return this;
	}

	public Matrix4 loadRotation(float x, float y, float z) {
		Matrix4 rx = new Matrix4();
		Matrix4 ry = new Matrix4();
		Matrix4 rz = new Matrix4();

		rz.elements[0][0] = Mathf.cos(z);
		rz.elements[0][1] = Mathf.sin(z);
		rz.elements[0][2] = 0;
		rz.elements[0][3] = 0;
		rz.elements[1][0] = Mathf.sin(z);
		rz.elements[1][1] = Mathf.cos(z);
		rz.elements[1][2] = 0;
		rz.elements[1][3] = 0;
		rz.elements[2][0] = 0;
		rz.elements[2][1] = 0;
		rz.elements[2][2] = 1;
		rz.elements[2][3] = 0;
		rz.elements[3][0] = 0;
		rz.elements[3][1] = 0;
		rz.elements[3][2] = 0;
		rz.elements[3][3] = 1;

		rx.elements[0][0] = 1;
		rx.elements[0][1] = 0;
		rx.elements[0][2] = 0;
		rx.elements[0][3] = 0;
		rx.elements[1][0] = 0;
		rx.elements[1][1] = Mathf.cos(x);
		rx.elements[1][2] = Mathf.sin(x);
		rx.elements[1][3] = 0;
		rx.elements[2][0] = 0;
		rx.elements[2][1] = Mathf.sin(x);
		rx.elements[2][2] = Mathf.cos(x);
		rx.elements[2][3] = 0;
		rx.elements[3][0] = 0;
		rx.elements[3][1] = 0;
		rx.elements[3][2] = 0;
		rx.elements[3][3] = 1;

		ry.elements[0][0] = Mathf.cos(y);
		ry.elements[0][1] = 0;
		ry.elements[0][2] = -Mathf.sin(y);
		ry.elements[0][3] = 0;
		ry.elements[1][0] = 0;
		ry.elements[1][1] = 1;
		ry.elements[1][2] = 0;
		ry.elements[1][3] = 0;
		ry.elements[2][0] = Mathf.sin(y);
		ry.elements[2][1] = 0;
		ry.elements[2][2] = Mathf.cos(y);
		ry.elements[2][3] = 0;
		ry.elements[3][0] = 0;
		ry.elements[3][1] = 0;
		ry.elements[3][2] = 0;
		ry.elements[3][3] = 1;

		elements = rz.mul(ry.mul(rx)).elements;

		return this;
	}

	public Matrix4 loadRotation(Vector3 forward, Vector3 up, Vector3 right) {
		elements[0][0] = right.x;
		elements[0][1] = right.y;
		elements[0][2] = right.z;
		elements[0][3] = 0;
		elements[1][0] = up.x;
		elements[1][1] = up.y;
		elements[1][2] = up.z;
		elements[1][3] = 0;
		elements[2][0] = forward.x;
		elements[2][1] = forward.y;
		elements[2][2] = forward.z;
		elements[2][3] = 0;
		elements[3][0] = 0;
		elements[3][1] = 0;
		elements[3][2] = 0;
		elements[3][3] = 1;

		return this;
	}

	public Matrix4 loadRotation(Vector3 forward, Vector3 up) {
		Vector3 f = forward.normalized();
		Vector3 r = up.normalized();

		r = Vector3.cross(r, f);

		Vector3 u = Vector3.cross(f, r);

		return loadRotation(f, u, r);
	}

	public Matrix4 loadScale(float x, float y, float z) {
		elements[0][0] = x;
		elements[0][1] = 0;
		elements[0][2] = 0;
		elements[0][3] = 0;
		elements[1][0] = 0;
		elements[1][1] = y;
		elements[1][2] = 0;
		elements[1][3] = 0;
		elements[2][0] = 0;
		elements[2][1] = 0;
		elements[2][2] = z;
		elements[2][3] = 0;
		elements[3][0] = 0;
		elements[3][1] = 0;
		elements[3][2] = 0;
		elements[3][3] = 1;

		return this;
	}

	public Matrix4 loadOrthographic(float left, float right, float bottom, float top, float near, float far) {
		float width = right - left;
		float height = top - bottom;
		float depth = far - near;

		elements[0][0] = 2.0f / width;
		elements[0][1] = 0.0f;
		elements[0][2] = 0.0f;
		elements[0][3] = -(right + left) / width;
		elements[1][0] = 0.0f;
		elements[1][1] = 2.0f / height;
		elements[1][2] = 0.0f;
		elements[1][3] = -(top + bottom) / height;
		elements[2][0] = 0.0f;
		elements[2][1] = 0.0f;
		elements[2][2] = -2.0f / depth;
		elements[2][3] = -(far + near) / depth;
		elements[3][0] = 0.0f;
		elements[3][1] = 0.0f;
		elements[3][2] = 0.0f;
		elements[3][3] = 1.0f;

		return this;
	}

	public Matrix4 loadPerspective(float fov, float aspectRatio, float near, float far) {
		float tan = Mathf.tan((Mathf.DEG_TO_RAD * fov) / 2.0f);
		float r = near - far;

		elements[0][0] = 1.0f / (tan * aspectRatio);
		elements[0][1] = 0.0f;
		elements[0][2] = 0.0f;
		elements[0][3] = 0.0f;
		elements[1][0] = 0.0f;
		elements[1][1] = 1.0f / tan;
		elements[1][2] = 0.0f;
		elements[1][3] = 0.0f;
		elements[2][0] = 0.0f;
		elements[2][1] = 0.0f;
		elements[2][2] = (-near - far) / r;
		elements[2][3] = 2.0f * far * near / r;
		elements[3][0] = 0.0f;
		elements[3][1] = 0.0f;
		elements[3][2] = 1.0f;
		elements[3][3] = 0.0f;

		return this;
	}

	public Vector3 transformPoint(Vector3 v) {
		return new Vector3(elements[0][0] * v.x + elements[0][1] * v.y + elements[0][2] * v.z + elements[0][3],
				elements[1][0] * v.x + elements[1][1] * v.y + elements[1][2] * v.z + elements[1][3],
				elements[2][0] * v.x + elements[2][1] * v.y + elements[2][2] * v.z + elements[2][3]);
	}

	public Matrix4 mul(Matrix4 m) {
		Matrix4 r = new Matrix4();

		r.elements[0][0] = elements[0][0] * m.elements[0][0] +
				elements[0][1] * m.elements[1][0] +
				elements[0][2] * m.elements[2][0] +
				elements[0][3] * m.elements[3][0];
		r.elements[0][1] = elements[0][0] * m.elements[0][1] +
				elements[0][1] * m.elements[1][1] +
				elements[0][2] * m.elements[2][1] +
				elements[0][3] * m.elements[3][1];
		r.elements[0][2] = elements[0][0] * m.elements[0][2] +
				elements[0][1] * m.elements[1][2] +
				elements[0][2] * m.elements[2][2] +
				elements[0][3] * m.elements[3][2];
		r.elements[0][3] = elements[0][0] * m.elements[0][3] +
				elements[0][1] * m.elements[1][3] +
				elements[0][2] * m.elements[2][3] +
				elements[0][3] * m.elements[3][3];
		r.elements[1][0] = elements[1][0] * m.elements[0][0] +
				elements[1][1] * m.elements[1][0] +
				elements[1][2] * m.elements[2][0] +
				elements[1][3] * m.elements[3][0];
		r.elements[1][1] = elements[1][0] * m.elements[0][1] +
				elements[1][1] * m.elements[1][1] +
				elements[1][2] * m.elements[2][1] +
				elements[1][3] * m.elements[3][1];
		r.elements[1][2] = elements[1][0] * m.elements[0][2] +
				elements[1][1] * m.elements[1][2] +
				elements[1][2] * m.elements[2][2] +
				elements[1][3] * m.elements[3][2];
		r.elements[1][3] = elements[1][0] * m.elements[0][3] +
				elements[1][1] * m.elements[1][3] +
				elements[1][2] * m.elements[2][3] +
				elements[1][3] * m.elements[3][3];
		r.elements[2][0] = elements[2][0] * m.elements[0][0] +
				elements[2][1] * m.elements[1][0] +
				elements[2][2] * m.elements[2][0] +
				elements[2][3] * m.elements[3][0];
		r.elements[2][1] = elements[2][0] * m.elements[0][1] +
				elements[2][1] * m.elements[1][1] +
				elements[2][2] * m.elements[2][1] +
				elements[2][3] * m.elements[3][1];
		r.elements[2][2] = elements[2][0] * m.elements[0][2] +
				elements[2][1] * m.elements[1][2] +
				elements[2][2] * m.elements[2][2] +
				elements[2][3] * m.elements[3][2];
		r.elements[2][3] = elements[2][0] * m.elements[0][3] +
				elements[2][1] * m.elements[1][3] +
				elements[2][2] * m.elements[2][3] +
				elements[2][3] * m.elements[3][3];
		r.elements[3][0] = elements[3][0] * m.elements[0][0] +
				elements[3][1] * m.elements[1][0] +
				elements[3][2] * m.elements[2][0] +
				elements[3][3] * m.elements[3][0];
		r.elements[3][1] = elements[3][0] * m.elements[0][1] +
				elements[3][1] * m.elements[1][1] +
				elements[3][2] * m.elements[2][1] +
				elements[3][3] * m.elements[3][1];
		r.elements[3][2] = elements[3][0] * m.elements[0][2] +
				elements[3][1] * m.elements[1][2] +
				elements[3][2] * m.elements[2][2] +
				elements[3][3] * m.elements[3][2];
		r.elements[3][3] = elements[3][0] * m.elements[0][3] +
				elements[3][1] * m.elements[1][3] +
				elements[3][2] * m.elements[2][3] +
				elements[3][3] * m.elements[3][3];

		return r;
	}

	public FloatBuffer toBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				buffer.put(get(i, j));
			}
		}

		buffer.flip();

		return buffer;
	}

	javax.vecmath.Matrix4f toVecmath() {
		float[] floats = new float[16];

		for (int i = 0; i < 16; i++) {
			floats[i] = elements[i / 4][i % 4];
		}

		return new javax.vecmath.Matrix4f(floats);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				sb.append(elements[i][j]);
				sb.append(' ');
			}

			sb.append('\n');
		}

		return sb.toString();
	}

	public static Matrix4 createOrthographic(float left, float right, float bottom, float top, float near, float far) {
		Matrix4 m = new Matrix4();

		return m.loadOrthographic(left, right, bottom, top, near, far);
	}

	public static Matrix4 createPerspective(float fov, float aspectRatio, float near, float far) {
		Matrix4 m = new Matrix4();

		return m.loadPerspective(fov, aspectRatio, near, far);
	}

	public static Matrix4 createIdentity() {
		return new Matrix4().loadIdentity();
	}

	public static Matrix4 createRotation(Vector3 forward, Vector3 up, Vector3 right) {
		Matrix4 m = new Matrix4();

		return m.loadRotation(forward, up, right);
	}

	public static Matrix4 createRotation(Vector3 forward, Vector3 up) {
		Matrix4 m = new Matrix4();

		return m.loadRotation(forward, up);
	}

	public static Matrix4 createRotation(float x, float y, float z) {
		Matrix4 m = new Matrix4();

		return m.loadRotation(x, y, z);
	}

	public static Matrix4 createTranslation(Vector3 translation) {
		return createTranslation(translation.x, translation.y, translation.z);
	}

	public static Matrix4 createTranslation(float x, float y, float z) {
		return new Matrix4().loadTranslation(x, y, z);
	}

	public static Matrix4 createScale(Vector3 scale) {
		return new Matrix4().loadScale(scale.x, scale.y, scale.z);
	}

	public static Matrix4 createScale(float x, float y, float z) {
		return new Matrix4().loadScale(x, y, z);
	}

}
