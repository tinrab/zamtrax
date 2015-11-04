package zamtrax;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class Time {

	private static double targetUPS;
	private static int fps;

	private static float deltaTime;
	private static float interpolation;

	static void setTargetUPS(double targetUPS) {
		Time.targetUPS = targetUPS;
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

	static void setDeltaTime(float deltaTime) {
		Time.deltaTime = deltaTime;
	}

	public static float getInterpolation() {
		return interpolation;
	}

	static void setInterpolation(float interpolation) {
		Time.interpolation = interpolation;
	}

	public static double currentNanos() {
		return glfwGetTime() * 1000000000.0;
	}

	public static double currentMicros() {
		return glfwGetTime() * 1000000.0;
	}

	public static double currentMillis() {
		return glfwGetTime() * 1000.0;
	}

	public static double currentSeconds() {
		return glfwGetTime();
	}

}
