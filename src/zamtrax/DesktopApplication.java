package zamtrax;

import org.omg.CORBA.TIMEOUT;

final class DesktopApplication implements Application {

	private Application.Listener applicationListener;
	private boolean isRunning;
	private Window window;

	public DesktopApplication(int windowWidth, int windowHeight, String title, boolean vSync, double targetUPS) {
		window = new Window(windowWidth, windowHeight, title, vSync);

		Time.getInstance().setTargetUPS((float) targetUPS);
	}

	@Override
	public void setApplicationListener(Application.Listener applicationListener) {
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

		Time time = Time.getInstance();

		final double frameTime = 1.0 / time.getTargetUPS();

		time.setDeltaTime((float) frameTime);

		double currentTime;
		double previousTime;
		double elapsed;

		double lag = 0.0;
		double lastFPSUpdate = 0.0;

		int framesProcessed = 0;

		previousTime = time.currentSeconds();

		while (isRunning && !window.isClosed()) {
			currentTime = time.currentSeconds();
			elapsed = currentTime - previousTime;

			lag += elapsed;
			while (lag > frameTime) {
				applicationListener.update(time.getDeltaTime());

				lag -= frameTime;
			}

			float lagOffset = (float) (lag / frameTime);
			applicationListener.render();

			framesProcessed++;

			if (currentTime - lastFPSUpdate >= 1.0) {
				time.setFPS(framesProcessed);
				framesProcessed = 0;
			}

			window.update();

			previousTime = currentTime;
		}

		dispose();
	}

	private void dispose() {
		applicationListener.dispose();
	}

}
