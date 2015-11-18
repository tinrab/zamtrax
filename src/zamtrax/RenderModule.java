package zamtrax;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

final class RenderModule extends Module implements Scene.Listener {

	private List<Renderer> renderers;

	RenderModule(Scene scene) {
		super(scene);

		scene.addSceneListener(this);

		renderers = new ArrayList<>();
	}

	private void addRenderer(Renderer renderer) {
		renderers.add(renderer);
	}

	private boolean removeRenderer(Renderer renderer) {
		return renderers.remove(renderer);
	}

	@Override
	public void render() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

		RenderPipeline renderPipeline = new RenderPipeline();

		Camera camera = Camera.getMainCamera();
		Camera.ClearFlags clearFlag = camera.getClearFlags();
		Matrix4 viewProjection = camera.getViewProjection();

		switch (clearFlag) {
			case SOLID_COLOR:
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				break;
			case DEPTH:
				glClear(GL_DEPTH_BUFFER_BIT);
				break;
			case NOTHING:
				glClear(GL_NONE);
				break;
		}

		if (clearFlag == Camera.ClearFlags.SOLID_COLOR) {
			Color clearColor = camera.getClearColor();

			glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		}

		for (Renderer renderer : renderers) {
			renderer.render(viewProjection);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public void onCreateGameObject(GameObject gameObject) {
	}

	@Override
	public void onDestroyGameObject(GameObject gameObject) {
	}

	@Override
	public void onAddComponent(Component component) {
		if (component instanceof Renderer) {
			addRenderer((Renderer) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof Renderer) {
			removeRenderer((Renderer) component);
		}
	}

}
