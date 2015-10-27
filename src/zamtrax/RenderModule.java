package zamtrax;

import zamtrax.components.Camera;

import static org.lwjgl.opengl.GL11.*;

public final class RenderModule extends Module {

	private SceneObject root;

	public RenderModule(Scene scene) {
		super(scene);

		this.root = scene.getRoot();
	}

	@Override
	public void render() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

		Camera camera = Camera.getMainCamera();
		Camera.ClearFlag clearFlag = camera.getClearFlag();
		Matrix4 viewProjection = camera.getViewProjection();

		glClear(clearFlag.getValue());

		if (clearFlag == Camera.ClearFlag.COLOR) {
			Color clearColor = camera.getClearColor();

			glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		}

		render(viewProjection, root);
	}

	private void render(Matrix4 viewProjection, SceneObject object) {
		object.getComponents().stream().filter(component -> component instanceof Renderable).forEach(component -> {
			Renderable renderable = (Renderable) component;

			renderable.render(viewProjection);
		});

		object.getChildren().forEach(child -> {
			render(viewProjection, child);
		});
	}

	@Override
	public void dispose() {
	}

}
