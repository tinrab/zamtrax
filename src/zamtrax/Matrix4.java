package zamtrax;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Matrix4 {

	public float[][] elements;

	public Matrix4() {
		elements = new float[4][4];
	}

	public Matrix4(Matrix4 m) {
		this();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				elements[i][j] = m.elements[i][j];
			}
		}
	}

	public float get(int i, int j) {
		return elements[i][j];
	}

	public void set(int i, int j, float value) {
		elements[i][j] = value;
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

	public Matrix4 loadRotationAroundX(float angle) {
		elements[0][0] = 1;
		elements[0][1] = 0;
		elements[0][2] = 0;
		elements[0][3] = 0;
		elements[1][0] = 0;
		elements[1][1] = Mathf.fastCos(angle);
		elements[1][2] = Mathf.fastSin(angle);
		elements[1][3] = 0;
		elements[2][0] = 0;
		elements[2][1] = Mathf.fastSin(angle);
		elements[2][2] = Mathf.fastCos(angle);
		elements[2][3] = 0;
		elements[3][0] = 0;
		elements[3][1] = 0;
		elements[3][2] = 0;
		elements[3][3] = 1;

		return this;
	}

	public Matrix4 loadRotationAroundY(float angle) {
		elements[0][0] = Mathf.fastCos(angle);
		elements[0][1] = 0;
		elements[0][2] = -Mathf.fastSin(angle);
		elements[0][3] = 0;
		elements[1][0] = 0;
		elements[1][1] = 1;
		elements[1][2] = 0;
		elements[1][3] = 0;
		elements[2][0] = Mathf.fastSin(angle);
		elements[2][1] = 0;
		elements[2][2] = Mathf.fastCos(angle);
		elements[2][3] = 0;
		elements[3][0] = 0;
		elements[3][1] = 0;
		elements[3][2] = 0;
		elements[3][3] = 1;

		return this;
	}

	public Matrix4 loadRotationAroundZ(float angle) {
		elements[0][0] = Mathf.fastCos(angle);
		elements[0][1] = Mathf.fastSin(angle);
		elements[0][2] = 0;
		elements[0][3] = 0;
		elements[1][0] = Mathf.fastSin(angle);
		elements[1][1] = Mathf.fastCos(angle);
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

	public Matrix4 loadRotation(float x, float y, float z) {
		elements = createRotationAroundZ(z).mul(createRotationAroundY(y).mul(createRotationAroundX(x))).elements;

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

	public Matrix4 transpose() {
		Matrix4 m = new Matrix4();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				m.elements[i][j] = elements[j][i];
			}
		}
		return m;
	}

	public Matrix4 inverse() {
		float[] mat = new float[16];
		float[] dst = new float[16];

		int k = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				mat[k++] = elements[i][j];
			}
		}

		float[] tmp = new float[12];

		float src[] = new float[16];

		float det;

		for (int i = 0; i < 4; i++) //>
		{
			src[i] = mat[i * 4];
			src[i + 4] = mat[i * 4 + 1];
			src[i + 8] = mat[i * 4 + 2];
			src[i + 12] = mat[i * 4 + 3];
		}

		tmp[0] = src[10] * src[15];
		tmp[1] = src[11] * src[14];
		tmp[2] = src[9] * src[15];
		tmp[3] = src[11] * src[13];
		tmp[4] = src[9] * src[14];
		tmp[5] = src[10] * src[13];
		tmp[6] = src[8] * src[15];
		tmp[7] = src[11] * src[12];
		tmp[8] = src[8] * src[14];
		tmp[9] = src[10] * src[12];
		tmp[10] = src[8] * src[13];
		tmp[11] = src[9] * src[12];

		dst[0] = tmp[0] * src[5] + tmp[3] * src[6] + tmp[4] * src[7];
		dst[0] -= tmp[1] * src[5] + tmp[2] * src[6] + tmp[5] * src[7];
		dst[1] = tmp[1] * src[4] + tmp[6] * src[6] + tmp[9] * src[7];
		dst[1] -= tmp[0] * src[4] + tmp[7] * src[6] + tmp[8] * src[7];
		dst[2] = tmp[2] * src[4] + tmp[7] * src[5] + tmp[10] * src[7];
		dst[2] -= tmp[3] * src[4] + tmp[6] * src[5] + tmp[11] * src[7];
		dst[3] = tmp[5] * src[4] + tmp[8] * src[5] + tmp[11] * src[6];
		dst[3] -= tmp[4] * src[4] + tmp[9] * src[5] + tmp[10] * src[6];
		dst[4] = tmp[1] * src[1] + tmp[2] * src[2] + tmp[5] * src[3];
		dst[4] -= tmp[0] * src[1] + tmp[3] * src[2] + tmp[4] * src[3];
		dst[5] = tmp[0] * src[0] + tmp[7] * src[2] + tmp[8] * src[3];
		dst[5] -= tmp[1] * src[0] + tmp[6] * src[2] + tmp[9] * src[3];
		dst[6] = tmp[3] * src[0] + tmp[6] * src[1] + tmp[11] * src[3];
		dst[6] -= tmp[2] * src[0] + tmp[7] * src[1] + tmp[10] * src[3];
		dst[7] = tmp[4] * src[0] + tmp[9] * src[1] + tmp[10] * src[2];
		dst[7] -= tmp[5] * src[0] + tmp[8] * src[1] + tmp[11] * src[2];

		tmp[0] = src[2] * src[7];
		tmp[1] = src[3] * src[6];
		tmp[2] = src[1] * src[7];
		tmp[3] = src[3] * src[5];
		tmp[4] = src[1] * src[6];
		tmp[5] = src[2] * src[5];
		tmp[6] = src[0] * src[7];
		tmp[7] = src[3] * src[4];
		tmp[8] = src[0] * src[6];
		tmp[9] = src[2] * src[4];
		tmp[10] = src[0] * src[5];
		tmp[11] = src[1] * src[4];

		dst[8] = tmp[0] * src[13] + tmp[3] * src[14] + tmp[4] * src[15];
		dst[8] -= tmp[1] * src[13] + tmp[2] * src[14] + tmp[5] * src[15];
		dst[9] = tmp[1] * src[12] + tmp[6] * src[14] + tmp[9] * src[15];
		dst[9] -= tmp[0] * src[12] + tmp[7] * src[14] + tmp[8] * src[15];
		dst[10] = tmp[2] * src[12] + tmp[7] * src[13] + tmp[10] * src[15];
		dst[10] -= tmp[3] * src[12] + tmp[6] * src[13] + tmp[11] * src[15];
		dst[11] = tmp[5] * src[12] + tmp[8] * src[13] + tmp[11] * src[14];
		dst[11] -= tmp[4] * src[12] + tmp[9] * src[13] + tmp[10] * src[14];
		dst[12] = tmp[2] * src[10] + tmp[5] * src[11] + tmp[1] * src[9];
		dst[12] -= tmp[4] * src[11] + tmp[0] * src[9] + tmp[3] * src[10];
		dst[13] = tmp[8] * src[11] + tmp[0] * src[8] + tmp[7] * src[10];
		dst[13] -= tmp[6] * src[10] + tmp[9] * src[11] + tmp[1] * src[8];
		dst[14] = tmp[6] * src[9] + tmp[11] * src[11] + tmp[3] * src[8];
		dst[14] -= tmp[10] * src[11] + tmp[2] * src[8] + tmp[7] * src[9];
		dst[15] = tmp[10] * src[10] + tmp[4] * src[8] + tmp[9] * src[9];
		dst[15] -= tmp[8] * src[9] + tmp[11] * src[10] + tmp[5] * src[8];

		det = src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2] + src[3] * dst[3];

		det = 1 / det;

		for (int j = 0; j < 16; j++) {
			dst[j] *= det;
		}

		Matrix4 m = new Matrix4();

		k = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				m.elements[i][j] = dst[k++];
			}
		}

		return m;
	}

	private final static FloatBuffer direct = BufferUtils.createFloatBuffer(16);

	public FloatBuffer toBuffer() {
		direct.clear();

		toBuffer(direct);

		direct.flip();

		return direct;
	}

	public void toBuffer(FloatBuffer buffer) {
		buffer.put(elements[0][0]).put(elements[1][0]).put(elements[2][0]).put(elements[3][0]);
		buffer.put(elements[0][1]).put(elements[1][1]).put(elements[2][1]).put(elements[3][1]);
		buffer.put(elements[0][2]).put(elements[1][2]).put(elements[2][2]).put(elements[3][2]);
		buffer.put(elements[0][3]).put(elements[1][3]).put(elements[2][3]).put(elements[3][3]);
	}

	public void toArray(float[] array, int start) {
		array[start++] = elements[0][0];
		array[start++] = elements[1][0];
		array[start++] = elements[2][0];
		array[start++] = elements[3][0];

		array[start++] = elements[0][1];
		array[start++] = elements[1][1];
		array[start++] = elements[2][1];
		array[start++] = elements[3][1];

		array[start++] = elements[0][2];
		array[start++] = elements[1][2];
		array[start++] = elements[2][2];
		array[start++] = elements[3][2];

		array[start++] = elements[0][3];
		array[start++] = elements[1][3];
		array[start++] = elements[2][3];
		array[start++] = elements[3][3];
	}

	javax.vecmath.Matrix4f toVecmath() {
		float[] floats = new float[16];

		for (int i = 0; i < 16; i++) {
			floats[i] = elements[i / 4][i % 4];
		}

		return new javax.vecmath.Matrix4f(floats);
	}

	public Matrix3 toMatrix3() {
		Matrix3 m = new Matrix3();

		m.elements[0][0] = elements[0][0];
		m.elements[0][1] = elements[0][1];
		m.elements[0][2] = elements[0][2];
		m.elements[1][0] = elements[1][0];
		m.elements[1][1] = elements[1][1];
		m.elements[1][2] = elements[1][2];
		m.elements[2][0] = elements[2][0];
		m.elements[2][1] = elements[2][1];
		m.elements[2][2] = elements[2][2];

		return m;
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

	public static Matrix4 createRotationAroundX(float angle) {
		Matrix4 m = new Matrix4();

		return m.loadRotationAroundX(angle);
	}

	public static Matrix4 createRotationAroundY(float angle) {
		Matrix4 m = new Matrix4();

		return m.loadRotationAroundY(angle);
	}

	public static Matrix4 createRotationAroundZ(float angle) {
		Matrix4 m = new Matrix4();

		return m.loadRotationAroundZ(angle);
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
