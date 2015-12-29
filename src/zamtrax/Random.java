package zamtrax;

public final class Random {

	private static java.util.Random random = new java.util.Random();

	public static float randomFloat() {
		return random.nextFloat();
	}

	public static Vector3 direction() {
		return new Vector3(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, random.nextFloat() - 0.5f).normalized();
	}

	public static Color color() {
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f);
	}

	public static int randomInteger() {
		return random.nextInt();
	}

	public static int randomInteger(int max) {
		return random.nextInt(max);
	}

	public static int randomInteger(int min, int max) {
		return min + random.nextInt(max - min);
	}

}
