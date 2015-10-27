package zamtrax;

public class Color {

	public float r, g, b, a;

	public Color() {
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}

	public Color(Color c) {
		this(c.r, c.g, c.b, c.a);
	}

	public Color(int rgba) {
		int a = rgba & 0xFF;
		int b = (rgba >> 8) & 0xFF;
		int g = (rgba >> 16) & 0xFF;
		int r = (rgba >> 24) & 0xFF;

		this.r = r / 255.0f;
		this.g = g / 255.0f;
		this.b = b / 255.0f;
		this.a = a / 255.0f;
	}

	public void set(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public float[] toArray() {
		return new float[]{r, g, b, a};
	}

	public static Color createWhite() {
		return new Color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static Color createBlack() {
		return new Color(0.0f, 0.0f, 0.0f, 1.0f);
	}

}
