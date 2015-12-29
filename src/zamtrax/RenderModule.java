package zamtrax;

import zamtrax.components.*;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public final class RenderModule extends Module implements Scene.Listener {

	private static RenderModule instance;

	public static RenderModule getInstance() {
		return instance;
	}

	private List<Renderer> renderers;

	private Color ambientLight;
	private List<DirectionalLight> directionalLights;

	RenderModule(Scene scene) {
		super(scene);
		instance = this;

		scene.addSceneListener(this);

		renderers = new ArrayList<>();

		directionalLights = new ArrayList<>();
		ambientLight = new Color(0.2f, 0.2f, 0.2f);
	}

	@Override
	public void render() {
		Camera camera = Camera.getMainCamera();
		Camera.ClearFlags clearFlag = camera.getClearFlags();
		Matrix4 viewProjection = camera.getViewProjection();

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

		switch (clearFlag) {
			case SOLID_COLOR:
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

				Color clearColor = camera.getClearColor();

				glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
				break;
			case DEPTH:
				glClear(GL_DEPTH_BUFFER_BIT);
				break;
			case NOTHING:
				glClear(GL_NONE);
				break;
		}

		// Ambient pass
		{
			ForwardAmbientShader forwardAmbient = ForwardAmbientShader.getInstance();

			forwardAmbient.bind();

			for (Renderer renderer : renderers) {
				forwardAmbient.updateUniforms(renderer.getTransform(), viewProjection, renderer.getMaterial(), ambientLight);

				renderer.render();
			}

			forwardAmbient.release();
		}

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glDepthMask(false);
		glDepthFunc(GL_EQUAL);

		// Directional pass
		{
			ForwardDirectionalShader forwardDirectional = ForwardDirectionalShader.getInstance();

			forwardDirectional.bind();

			for (DirectionalLight directionalLight : directionalLights) {
				for (Renderer renderer : renderers) {
					forwardDirectional.updateUniforms(renderer.getTransform(), viewProjection, renderer.getMaterial(), directionalLight);

					renderer.render();
				}
			}

			forwardDirectional.release();
		}

		glDepthFunc(GL_LESS);
		glDepthMask(true);
		glDisable(GL_BLEND);
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
			renderers.add((Renderer) component);
		} else if (component instanceof DirectionalLight) {
			directionalLights.add((DirectionalLight) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof Renderer) {
			renderers.remove(component);
		} else if (component instanceof DirectionalLight) {
			directionalLights.remove(component);
		}
	}

	public void setAmbientLight(Color ambientLight) {
		this.ambientLight = ambientLight;
	}

	public Color getAmbientLight() {
		return ambientLight;
	}

}
