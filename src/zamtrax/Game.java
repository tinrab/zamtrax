package zamtrax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Game implements Application.Listener {

	private static Game instance;

	private Application application;
	private Map<Class, Scene> sceneMap;
	private Scene currentScene;
	private Class startScene;

	private Game(Application application, List<Scene> scenes) {
		this.application = application;
		application.setApplicationListener(this);

		sceneMap = new HashMap<>();

		for (Scene scene : scenes) {
			sceneMap.put(scene.getClass(), scene);
		}

		startScene = scenes.get(scenes.size() - 1).getClass();
	}

	public void start() {
		application.start();
	}

	public void exit() {
		if (currentScene != null) {
			currentScene.onExit();
		}

		application.exit();
	}

	public void enterScene(Class sceneClass) {
		Scene scene = sceneMap.get(sceneClass);

		if (scene == null) {
			throw new RuntimeException("Scene not found");
		}

		if (currentScene != null) {
			currentScene.onExit();
			currentScene.dispose();
		}

		currentScene = scene;
		currentScene.onEnter();
	}

	@Override
	public void create() {
		enterScene(startScene);
	}

	@Override
	public void update(float delta) {
		currentScene.update(delta);
	}

	@Override
	public void render() {
		currentScene.render();
	}

	@Override
	public void dispose() {
		if (currentScene != null) {
			currentScene.dispose();
		}
	}

	public Scene getCurrentScene() {
		return currentScene;
	}

	public static Game getInstance() {
		return instance;
	}

	public static int getScreenWidth() {
		return instance.application.getScreenWidth();
	}

	public static int getScreenHeight() {
		return instance.application.getScreenHeight();
	}

	public static class Starter {

		private static final int DEFAULT_WINDOW_WIDTH = 480;
		private static final int DEFAULT_WINDOW_HEIGHT = 360;
		private static final String DEFAULT_TITLE = "Zamtrax";
		private static final boolean DEFAULT_VSYNC = false;
		private static final double DEFAULT_TARGET_UPS = 60.0;

		private int windowWidth, windowHeight;
		private String title;
		private boolean vSync;
		private double targetUPS;
		private List<Scene> scenes;
		private ClassLoader classLoader;

		public Starter() {
			windowWidth = DEFAULT_WINDOW_WIDTH;
			windowHeight = DEFAULT_WINDOW_HEIGHT;
			title = DEFAULT_TITLE;
			vSync = DEFAULT_VSYNC;
			targetUPS = DEFAULT_TARGET_UPS;
			scenes = new ArrayList<>();
		}

		public Starter setWindowSize(int width, int height) {
			windowWidth = width;
			windowHeight = height;

			return this;
		}

		public Starter setTitle(String title) {
			this.title = title;

			return this;
		}

		public Starter setVSync(boolean vSync) {
			this.vSync = vSync;

			return this;
		}

		public Starter setTargetUPS(double targetUPS) {
			this.targetUPS = targetUPS;

			return this;
		}

		public Starter addScene(Scene scene) {
			scenes.add(scene);

			return this;
		}

		public Starter setClassLoader(ClassLoader classLoader) {
			this.classLoader = classLoader;

			return this;
		}

		public void start() {
			try {
				Application application = new DesktopApplication(windowWidth, windowHeight, title, vSync, targetUPS);

				instance = new Game(application, scenes);
				application.setApplicationListener(instance);

				Resources.init(classLoader);

				instance.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
