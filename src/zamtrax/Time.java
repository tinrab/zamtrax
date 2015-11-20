package zamtrax;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class Time {

	private static Time instance;

	public static Time getInstance() {
		if (instance == null) {
			synchronized (Time.class) {
				if (instance == null) {
					instance = new Time();
				}
			}
		}

		return instance;
	}

	private double targetUPS;
	private int fps;
	private float deltaTime;

	void setTargetUPS(double targetUPS) {
		this.targetUPS = targetUPS;
	}

	double getTargetUPS() {
		return targetUPS;
	}

	void setFPS(int fps) {
		this.fps = fps;
	}

	public int getFPS() {
		return fps;
	}

	public float getDeltaTime() {
		return deltaTime;
	}

	void setDeltaTime(float deltaTime) {
		this.deltaTime = deltaTime;
	}

	public double currentNanos() {
		return glfwGetTime() * 1000000000.0;
	}

	public double currentMicros() {
		return glfwGetTime() * 1000000.0;
	}

	public double currentMillis() {
		return glfwGetTime() * 1000.0;
	}

	public double currentSeconds() {
		return glfwGetTime();
	}

}
