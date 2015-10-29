package zamtrax;

import zamtrax.components.Camera;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public final class RenderModule extends Module {

	private List<Renderer> renderers;

	public RenderModule(Scene scene) {
		super(scene);

		renderers = new ArrayList<>();
	}

	public void addRenderer(Renderer renderer) {
		renderers.add(renderer);
	}

	public boolean removeRenderer(Renderer renderer) {
		return renderers.remove(renderer);
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

		for (Renderer renderer : renderers) {
			renderer.render(viewProjection);
		}
	}

	@Override
	public void dispose() {
	}

}
