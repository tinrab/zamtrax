package zamtrax.rendering;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.Renderer;
import zamtrax.resources.*;

import java.util.*;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL32.*;

public final class RenderModule extends Module implements Scene.Listener {

	private static RenderModule instance;

	public static RenderModule getInstance() {
		return instance;
	}

	private List<Renderer> renderers;
	private Map<Integer, Filter> passFilters;
	private List<Filter> screenFilters;
	private Color ambientLight;
	private List<Light> lights;

	private RenderState renderState;
	private VertexArray filterVertexArray;
	private Shader shadowMapGenerator;
	private Matrix4 biasMatrix;
	private Texture shadowMap, shadowMapTempTarget;
	private Texture passTextureA;

	private Filter nullFilter;

	public RenderModule(Scene scene) {
		super(scene);
		instance = this;

		scene.addSceneListener(this);

		renderers = new ArrayList<>();
		passFilters = new HashMap<>();
		screenFilters = new ArrayList<>();

		lights = new ArrayList<>();
		ambientLight = new Color(0.05f, 0.05f, 0.05f);

		renderState = new RenderState();
	}

	@Override
	public void onCreate() {
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		filterVertexArray = new VertexArray(6, new BindingInfo(AttributeType.POSITION, AttributeType.UV));

		passTextureA = new Texture(GL_TEXTURE_2D, Game.getScreenWidth(), Game.getScreenHeight(),
				new int[]{GL_NEAREST, GL_NEAREST}, true,
				new int[]{GL_RGBA, GL_DEPTH_COMPONENT32},
				new int[]{GL_BGRA, GL_DEPTH_COMPONENT},
				new int[]{GL_COLOR_ATTACHMENT0_EXT, GL_DEPTH_ATTACHMENT_EXT});

		shadowMapGenerator = new Shader.Builder()
				.setFragmentShaderSource(Resources.loadText("shaders/shadowMapGenerator.fs", getClass().getClassLoader()))
				.setVertexShaderSource(Resources.loadText("shaders/shadowMapGenerator.vs", getClass().getClassLoader()))
				.build();
		biasMatrix = Matrix4.createScale(0.5f, 0.5f, 0.5f).mul(Matrix4.createTranslation(1.0f, 1.0f, 1.0f));
		shadowMap = new Texture(GL_TEXTURE_2D, 1024, 1024, GL_LINEAR, true, GL_RGB16, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);
		shadowMapTempTarget = new Texture(GL_TEXTURE_2D, 1024, 1024, GL_LINEAR, true, GL_RGB16, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);

		nullFilter = new Filter("filters/null.filter");
	}

	@Override
	public void render() {
		Camera camera = Camera.getMainCamera();
		Matrix4 viewProjection = camera.getViewProjection();

		//passTextureA.bindAsRenderTarget();

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		ambientPass(viewProjection);

		lightingPass(viewProjection);

		screenFilters.forEach(screenFilter -> {
			if (screenFilter.isEnabled()) {
				glEnable(GL_BLEND);
				glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

				applyFilter(screenFilter, null, null);

				glDisable(GL_BLEND);
			}
		});

		//applyFilter(nullFilter, passTextureA, null);

		{
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glDisable(GL_DEPTH_TEST);
			Debug debug = Debug.getInstance();
			debug.drawLine(Vector3.ZERO, new Vector3(1, 0, 0), 4, Color.createRed());
			debug.drawLine(Vector3.ZERO, new Vector3(0, 1, 0), 4, Color.createGreen());
			debug.drawLine(Vector3.ZERO, new Vector3(0, 0, 1), 4, Color.createBlue());
			debug.drawGrid(Vector3.ZERO, 16, 16, 1.0f, new Color(1.0f, 1.0f, 1.0f, 0.1f));
			glEnable(GL_DEPTH_TEST);
			glDisable(GL_BLEND);

		}
	}

	private void ambientPass(Matrix4 viewProjection) {
		renderState.clear();

		renderState.setViewProjection(viewProjection);
		renderState.setAmbientIntenstiy(ambientLight);

		for (Renderer renderer : renderers) {
			renderState.setRenderer(renderer);

			Shader shader = renderer.getMaterial().getShader();

			shader.bind();

			shader.updateUniforms(renderState);
			renderer.render();

			shader.release();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	private void lightingPass(Matrix4 viewProjection) {
		for (Light light : lights) {
			renderState.clear();

			shadowMap.bindAsRenderTarget();
			glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			if (light.getShadows() == Light.Shadows.HARD || light.getCookie() != null) {
				shadowPass(light);
			}

			//passTextureA.bindAsRenderTarget();
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);

			renderState.setViewProjection(viewProjection);
			renderState.setLight(light);
			renderState.setShadowMap(shadowMap);

			for (Renderer renderer : renderers) {
				Shader shader = renderer.getMaterial().getShader();

				shader.bind();

				renderState.setRenderer(renderer);

				shader.updateUniforms(renderState);

				renderer.render();

				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, 0);
				shader.release();
			}

			glDepthFunc(GL_LESS);
			glDepthMask(true);
			glDisable(GL_BLEND);
		}

		/*
		for (Light light : lights) {
			renderState.clear();

			shadowMap.bindAsRenderTarget();
			glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			if (light.getShadows() == Light.Shadows.HARD || light.getCookie() != null) {
				shadowPass(light);
			}

			//screenTexture.bindAsRenderTarget();
			passTextureA.bindAsRenderTarget();

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);

			renderState.setViewProjection(viewProjection);
			renderState.setLight(light);
			renderState.setShadowMap(shadowMap);

			renderAll();

			glDepthFunc(GL_LESS);
			glDepthMask(true);
			glDisable(GL_BLEND);
		}
		*/
	}

	private void renderAll() {
		for (Renderer renderer : renderers) {
			renderState.setRenderer(renderer);

			Shader shader = renderer.getMaterial().getShader();

			shader.bind();

			shader.updateUniforms(renderState);
			renderer.render();

			shader.release();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

	private void shadowPass(Light light) {
		Matrix4 lightViewProjection = light.getShadowViewProjection();

		glEnable(GL_DEPTH_CLAMP);
		glCullFace(GL_FRONT);

		shadowMapGenerator.bind();

		renderers.forEach(renderer -> {
			if (renderer.getMaterial().getShader().castsShadows()) {
				shadowMapGenerator.setUniform("MVP", lightViewProjection.mul(renderer.getTransform().getLocalToWorldMatrix()));

				renderer.render();
			}
		});

		shadowMapGenerator.release();

		glCullFace(GL_BACK);
		glDisable(GL_DEPTH_CLAMP);

		/*
		if (light.getShadowSoftness() != 0.0f) {
			blurShadowMap(light.getShadowSoftness());
		}
		*/

		renderState.setLightViewProjection(biasMatrix.mul(lightViewProjection));
	}

	private void applyFilter(Filter filter, Texture source, Texture destination) {
		if (source != null && source == destination) {
			throw new RuntimeException("source and destination cannot be the same");
		}

		if (destination == null) {
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
		} else {
			destination.bindAsRenderTarget();
		}

		filterVertexArray.clear();

		filterVertexArray.put(-1, -1, 0, 1).put(0, 0);
		filterVertexArray.put(-1, 1, 0, 1).put(0, 1);
		filterVertexArray.put(1, 1, 0, 1).put(1, 1);

		filterVertexArray.put(-1, -1, 0, 1).put(0, 0);
		filterVertexArray.put(1, 1, 0, 1).put(1, 1);
		filterVertexArray.put(1, -1, 0, 1).put(1, 0);

		glClear(GL_DEPTH_BUFFER_BIT);

		filter.bind();
		filter.updateUniforms(source, renderState);

		filterVertexArray.render(GL_TRIANGLES, 0, 6);

		if (source != null) {
			source.release();
		}

		filter.release();

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void dispose() {
		passTextureA.dispose();
		shadowMap.dispose();
		shadowMapGenerator.dispose();
		shadowMapTempTarget.dispose();
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
		} else if (component instanceof Light) {
			lights.add((Light) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof Renderer) {
			renderers.remove(component);
		} else if (component instanceof Light) {
			lights.remove(component);
		}
	}

	public void setAmbientLight(Color ambientLight) {
		this.ambientLight = ambientLight;
	}

	public Color getAmbientLight() {
		return ambientLight;
	}

	public void consolidate() {
		renderers.sort((r1, r2) -> r1.getMaterial().getShader().getPass() - r2.getMaterial().getShader().getPass());
	}

	public void setFilter(int pass, String pathname) {
		passFilters.put(pass, new Filter(pathname));
	}

	public void addScreenFilter(Filter filter) {
		screenFilters.add(filter);
	}

}
