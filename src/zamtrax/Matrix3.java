package zamtrax;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Matrix3 {

	public float[][] elements;

	public Matrix3() {
		elements = new float[3][3];
	}

	public float get(int i, int j) {
		return elements[i][j];
	}

	public Matrix3 loadIdentity() {
		elements[0][0] = 1;
		elements[0][1] = 0;
		elements[0][2] = 0;
		elements[1][0] = 0;
		elements[1][1] = 1;
		elements[1][2] = 0;
		elements[2][0] = 0;
		elements[2][1] = 0;
		elements[2][2] = 1;

		return this;
	}

	public Matrix3 invert() {
		float a00 = elements[0][0];
		float a01 = elements[0][1];
		float a02 = elements[0][2];
		float a10 = elements[1][0];
		float a11 = elements[1][1];
		float a12 = elements[1][2];
		float a20 = elements[2][0];
		float a21 = elements[2][1];
		float a22 = elements[2][2];

		float b01 = a22 * a11 - a12 * a21;
		float b11 = -a22 * a10 + a12 * a20;
		float b21 = a21 * a10 - a11 * a20;

		float det = a00 * b01 + a01 * b11 + a02 * b21;

		if (det == 0.0f) {
			return null;
		}

		Matrix3 m = new Matrix3();
		det = 1.0f / det;

		m.elements[0][0] = b01 * det;
		m.elements[0][1] = (-a22 * a01 + a02 * a21) * det;
		m.elements[0][2] = (a12 * a01 - a02 * a11) * det;
		m.elements[1][0] = b11 * det;
		m.elements[1][1] = (a22 * a00 - a02 * a20) * det;
		m.elements[1][2] = (-a12 * a00 + a02 * a10) * det;
		m.elements[2][0] = b21 * det;
		m.elements[2][1] = (-a21 * a00 + a01 * a20) * det;
		m.elements[2][2] = (a11 * a00 - a01 * a10) * det;

		return m;
	}

	public Matrix3 transpose(){
		Matrix3 m = new Matrix3();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				m.elements[j][i] = elements[i][j];
			}
		}

		return m;
	}

	public FloatBuffer toBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(9);

		/*
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				buffer.put(get(i, j));
			}
		}
		*/
		buffer.put(get(0, 0));
		buffer.put(get(0, 1));
		buffer.put(get(0, 2));
		buffer.put(get(1, 0));
		buffer.put(get(1, 1));
		buffer.put(get(1, 2));
		buffer.put(get(2, 0));
		buffer.put(get(2, 1));
		buffer.put(get(2, 2));

		buffer.flip();

		return buffer;
	}

}
