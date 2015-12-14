package zamtrax;

import zamtrax.components.*;
import zamtrax.resources.Material;
import zamtrax.resources.Shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public final class RenderModule extends Module implements Scene.Listener {

	private static RenderModule instance;

	public static RenderModule getInstance() {
		return instance;
	}

	private List<Renderer> zombieRenderers;
	private Map<Material, List<Renderer>> renderersForMaterial;
	private List<PointLight> pointLights;
	private List<SpotLight> spotLights;
	private Color ambientLight;

	RenderModule(Scene scene) {
		super(scene);
		instance = this;

		scene.addSceneListener(this);

		zombieRenderers = new ArrayList<>();
		renderersForMaterial = new HashMap<>();
		pointLights = new ArrayList<>();
		spotLights = new ArrayList<>();
		ambientLight = new Color(0.5f, 0.5f, 0.5f);
	}

	@Override
	public void render() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

		Camera camera = Camera.getMainCamera();
		Camera.ClearFlags clearFlag = camera.getClearFlags();
		Matrix4 projection = camera.getProjectionMatrix();

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

		for (Map.Entry<Material, List<Renderer>> e : renderersForMaterial.entrySet()) {
			Material material = e.getKey();
			Shader shader = material.getShader();

			material.bind();

			for (Renderer renderer : e.getValue()) {
				Transform transform = renderer.getTransform();
				Matrix4 modelView = transform.getLocalToWorldMatrix();
				Matrix3 normalMatrix = modelView.toMatrix3().invert().transpose();

				shader.setUniform("P", true, projection);
				shader.setUniform("MV", true, modelView);
				shader.setUniform("N", true, normalMatrix);

				shader.setUniform("ambientLight", new Vector3(ambientLight.r, ambientLight.g, ambientLight.b));
				shader.setPointLights(pointLights);
				shader.setSpotLights(spotLights);

				renderer.render();
			}

			material.unbind();
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
			zombieRenderers.add((Renderer) component);
		} else if (component instanceof PointLight) {
			pointLights.add((PointLight) component);
		} else if (component instanceof SpotLight) {
			spotLights.add((SpotLight) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof Renderer) {
			Renderer renderer = (Renderer) component;

			zombieRenderers.remove(renderer);

			List<Renderer> renderers = renderersForMaterial.get(renderer.getMaterial());

			renderers.remove(renderer);
		} else if (component instanceof PointLight) {
			pointLights.remove(component);
		} else if (component instanceof SpotLight) {
			spotLights.remove(component);
		}
	}

	public void consolidate() {
		for (Renderer renderer : zombieRenderers) {
			Material material = renderer.getMaterial();

			if (renderersForMaterial.containsKey(material)) {
				List<Renderer> renderers = renderersForMaterial.get(material);

				renderers.add(renderer);
			} else {
				List<Renderer> renderers = new ArrayList<>();

				renderers.add(renderer);

				renderersForMaterial.put(material, renderers);
			}
		}

		zombieRenderers.clear();
	}

	public void setAmbientLight(Color ambientLight) {
		this.ambientLight = ambientLight;
	}

	public Color getAmbientLight() {
		return ambientLight;
	}

}
