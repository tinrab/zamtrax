package zamtrax;

public final class Time {

	private static double targetUPS;
	private static int fps;

	private static float deltaTime;

	static void setTargetUPS(double targetUPS) {
		Time.targetUPS = targetUPS;
		deltaTime = (float) (1.0 / targetUPS);
	}

	static double getTargetUPS() {
		return targetUPS;
	}

	static void setFPS(int fps) {
		Time.fps = fps;
	}

	public static int getFPS() {
		return fps;
	}

	public static float getDeltaTime() {
		return deltaTime;
	}

}
