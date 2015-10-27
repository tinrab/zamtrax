package zamtrax;

final class DesktopApplication implements Application {

	private ApplicationListener applicationListener;
	private Thread thread;
	private boolean isRunning;
	private Window window;

	public DesktopApplication(int windowWidth, int windowHeight, String title, boolean vSync, double targetUPS) {
		thread = new Thread(runnable);
		window = new Window(windowWidth, windowHeight, title, vSync);

		Time.setTargetUPS((float) targetUPS);
	}

	@Override
	public void setApplicationListener(ApplicationListener applicationListener) {
		this.applicationListener = applicationListener;
	}

	@Override
	public void start() {
		if (isRunning || thread.isAlive()) {
			return;
		}

		thread.start();
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

	private Runnable runnable = () -> {
		window.show();

		isRunning = true;

		applicationListener.create();

		long lastTime = System.nanoTime();
		double ns = 1000000000.0 / Time.getTargetUPS();
		int frames = 0;
		long timer = System.currentTimeMillis();
		double delta = 0.0;

		while (isRunning && !window.isClosed()) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			while (delta >= 1.0) {
				update();
				delta--;
			}

			frames++;
			render();

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;

				Time.setFPS(frames);

				frames = 0;
			}
		}

		dispose();
	};

	private void update() {
		window.processEvents();
		applicationListener.update();
	}

	private void render() {
		applicationListener.render();
		window.render();
	}

	private void dispose() {
		applicationListener.dispose();
	}

}
