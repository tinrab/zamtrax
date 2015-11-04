package zamtrax;

import org.omg.CORBA.TIMEOUT;

final class DesktopApplication implements Application {

	private ApplicationListener applicationListener;
	private boolean isRunning;
	private Window window;

	public DesktopApplication(int windowWidth, int windowHeight, String title, boolean vSync, double targetUPS) {
		window = new Window(windowWidth, windowHeight, title, vSync);

		Time.setTargetUPS((float) targetUPS);
	}

	@Override
	public void setApplicationListener(ApplicationListener applicationListener) {
		this.applicationListener = applicationListener;
	}

	@Override
	public void start() {
		if (isRunning) {
			return;
		}

		isRunning = true;
		run();
	}

	@Override
	public void exit() {
		isRunning = false;

		System.exit(0);
	}

	@Override
	public int getScreenWidth() {
		return window.getWidth();
	}

	@Override
	public int getScreenHeight() {
		return window.getHeight();
	}

	private void run() {
		window.show();

		isRunning = true;

		applicationListener.create();

		final double frameTime = 1.0 / Time.getTargetUPS();
		final int maxSkippedFrames = 10;

		double currentTime, previousTime, elapsedTime;

		double lag = 0.0;
		double lastFPSUpdate = 0.0;

		int frames = 0;
		int skippedFrames = 0;

		previousTime = Time.currentSeconds();

		while (isRunning && !window.isClosed()) {
			currentTime = Time.currentSeconds();
			elapsedTime = currentTime - previousTime;

			lag += elapsedTime;

			while (lag > frameTime && skippedFrames < maxSkippedFrames) {
				Time.setDeltaTime((float) frameTime);
				applicationListener.update();

				lag -= frameTime;
				skippedFrames++;
			}

			Time.setInterpolation((float) (lag / frameTime));
			applicationListener.render();

			frames++;

			if (currentTime - lastFPSUpdate >= 1.0) {
				Time.setFPS(frames);
				frames = 0;
				lastFPSUpdate = currentTime;
			}

			window.render();
			window.processEvents();

			skippedFrames = 0;
			previousTime = currentTime;
		}

		dispose();
	}

	private void dispose() {
		applicationListener.dispose();
	}

}
