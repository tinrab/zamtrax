package zamtrax;

public final class Mathf {

	public static final float PI = 3.14159265359f;
	public static final float PI2 = PI * 2.0f;
	public static final float RAD_TO_DEG = 180.0f / PI;
	public static final float DEG_TO_RAD = PI / 180.0f;

	public static float sqrt(float x) {
		return (float) Math.sqrt(x);
	}

	public static float tan(float x) {
		return (float) Math.tan(x);
	}

	public static float atan2(float y, float x) {
		return (float) Math.atan2(y, x);
	}

	public static float asin(float x) {
		return (float) Math.asin(x);
	}

	public static float acos(float x) {
		return (float) Math.acos(x);
	}

	public static float abs(float x) {
		return x < 0.0f ? -x : x;
	}

	public static float clamp(float x, float min, float max) {
		return x < min ? min : x > max ? max : x;
	}

	public static float sin(float x) {
		return (float) Math.sin(x);
	}

	public static float cos(float x) {
		return (float) Math.cos(x);
	}

	// Credits http://www.java-gaming.org/index.php?topic=24191.0
	public static float fastSin(float x) {
		return sin[(int) (x * radToIndex) & SIN_MASK];
	}

	public static float fastCos(float x) {
		return cos[(int) (x * radToIndex) & SIN_MASK];
	}

	private static final float RAD, DEG;
	private static final int SIN_BITS, SIN_MASK, SIN_COUNT;
	private static final float radFull, radToIndex;
	private static final float degFull, degToIndex;
	private static final float[] sin, cos;

	static {
		RAD = (float) Math.PI / 180.0f;
		DEG = 180.0f / (float) Math.PI;

		SIN_BITS = 12;
		SIN_MASK = ~(-1 << SIN_BITS);
		SIN_COUNT = SIN_MASK + 1;

		radFull = (float) (Math.PI * 2.0);
		degFull = (float) (360.0);
		radToIndex = SIN_COUNT / radFull;
		degToIndex = SIN_COUNT / degFull;

		sin = new float[SIN_COUNT];
		cos = new float[SIN_COUNT];

		for (int i = 0; i < SIN_COUNT; i++) {
			sin[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
			cos[i] = (float) Math.cos((i + 0.5f) / SIN_COUNT * radFull);
		}

		// Four cardinal directions (credits: Nate)
		for (int i = 0; i < 360; i += 90) {
			sin[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * Math.PI / 180.0);
			cos[(int) (i * degToIndex) & SIN_MASK] = (float) Math.cos(i * Math.PI / 180.0);
		}
	}

	public static float floor(float x) {
		return (float) Math.floor(x);
	}

	public static int floorToInt(float x) {
		return (int) floor(x);
	}

	public static int roundToInt(float x) {
		return Math.round(x);
	}

	public static float sign(float x) {
		return x < 0.0f ? -1.0f : x > 0.0f ? 1.0f : 0.0f;
	}

	public static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

}
