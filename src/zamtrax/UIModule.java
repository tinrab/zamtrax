package zamtrax;

import zamtrax.components.Camera;
import zamtrax.ui.Canvas;
import zamtrax.ui.Graphic;
import zamtrax.ui.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class UIModule extends Module implements Scene.Listener {

	private Canvas canvas;
	private List<Graphic> graphics;
	private SpriteBatch spriteBatch;

	protected UIModule(Scene scene) {
		super(scene);

		graphics = new ArrayList<>();

		scene.addSceneListener(this);
	}

	@Override
	public void render() {
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR);
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

		spriteBatch.begin();

		graphics.forEach(graphic -> graphic.render(spriteBatch));

		spriteBatch.end();

		glDisable(GL_BLEND);
	}

	@Override
	public void onCreateGameObject(GameObject gameObject) {
	}

	@Override
	public void onDestroyGameObject(GameObject gameObject) {
	}

	@Override
	public void onAddComponent(Component component) {
		if (component instanceof Canvas) {
			this.canvas = (Canvas) component;
			spriteBatch = canvas.getSpriteBatch();
		} else if (component instanceof Graphic) {
			graphics.add((Graphic) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof Canvas) {
			this.canvas = null;

			canvas.getGameObject().getChildren().forEach(child -> child.destroy());
		} else if (component instanceof Graphic) {
			graphics.remove(component);
		}
	}

	@Override
	public void dispose() {
	}

}
