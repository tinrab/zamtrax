package zamtrax;

import zamtrax.components.*;
import zamtrax.resources.*;
import zamtrax.ui.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public final class RenderModule extends Module implements Scene.Listener {

	private static RenderModule instance;

	public static RenderModule getInstance() {
		return instance;
	}

	private List<Renderer> renderers;

	private Color ambientLight;
	private List<DirectionalLight> directionalLights;
	private List<PointLight> pointLights;
	private List<SpotLight> spotLights;

	RenderModule(Scene scene) {
		super(scene);
		instance = this;

		scene.addSceneListener(this);

		renderers = new ArrayList<>();

		directionalLights = new ArrayList<>();
		pointLights = new ArrayList<>();
		spotLights = new ArrayList<>();
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
		glDisable(GL_ALPHA_TEST);

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

		// Point pass
		{
			ForwardPointShader forwardPoint = ForwardPointShader.getInstance();

			forwardPoint.bind();

			for (PointLight pointLight : pointLights) {
				for (Renderer renderer : renderers) {
					forwardPoint.updateUniforms(renderer.getTransform(), viewProjection, renderer.getMaterial(), pointLight);

					renderer.render();
				}
			}

			forwardPoint.release();
		}

		// Spot pass
		{
			ForwardSpotShader forwardSpot = ForwardSpotShader.getInstance();

			forwardSpot.bind();

			for (SpotLight spotLight : spotLights) {
				for (Renderer renderer : renderers) {
					forwardSpot.updateUniforms(renderer.getTransform(), viewProjection, renderer.getMaterial(), spotLight);

					renderer.render();
				}
			}

			forwardSpot.release();
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
		} else if (component instanceof PointLight) {
			pointLights.add((PointLight) component);
		} else if (component instanceof SpotLight) {
			spotLights.add((SpotLight) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof Renderer) {
			renderers.remove(component);
		} else if (component instanceof DirectionalLight) {
			directionalLights.remove(component);
		} else if (component instanceof PointLight) {
			pointLights.remove(component);
		} else if (component instanceof SpotLight) {
			spotLights.remove(component);
		}
	}

	public void setAmbientLight(Color ambientLight) {
		this.ambientLight = ambientLight;
	}

	public Color getAmbientLight() {
		return ambientLight;
	}

}
