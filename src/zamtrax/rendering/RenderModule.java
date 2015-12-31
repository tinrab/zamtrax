package zamtrax.rendering;

import zamtrax.*;
import zamtrax.components.*;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL32.*;

public final class RenderModule extends Module implements Scene.Listener {

	private static RenderModule instance;

	public static RenderModule getInstance() {
		return instance;
	}

	private List<Renderer> renderers;

	private Color ambientLight;
	private List<Light> lights;

	private RenderState renderState;

	private ForwardAmbientShader forwardAmbient;
	private ForwardDirectionalShader forwardDirectional;
	private ForwardSpotShader forwardSpot;
	private ForwardPointShader forwardPoint;

	private Texture shadowMap;
	private Texture shadowMapTempTarget;
	private ShadowMapGeneratorShader shadowMapGenerator;
	private Matrix4 biasMatrix;

	private VertexArray filterVertexArray;
	private GaussBlur gaussBlur;

	private Texture screenTexture;

	public RenderModule(Scene scene) {
		super(scene);
		instance = this;

		scene.addSceneListener(this);

		renderers = new ArrayList<>();

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

		forwardDirectional = ForwardDirectionalShader.getInstance();
		forwardAmbient = ForwardAmbientShader.getInstance();
		forwardSpot = ForwardSpotShader.getInstance();
		forwardPoint = ForwardPointShader.getInstance();

		shadowMap = new Texture(GL_TEXTURE_2D, 1024, 1024, GL_LINEAR, true, GL_RGB16, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);
		shadowMapTempTarget = new Texture(GL_TEXTURE_2D, 1024, 1024, GL_LINEAR, true, GL_RGB16, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);

		shadowMapGenerator = ShadowMapGeneratorShader.getInstance();
		biasMatrix = Matrix4.createScale(0.5f, 0.5f, 0.5f).mul(Matrix4.createTranslation(1.0f, 1.0f, 1.0f));

		filterVertexArray = new VertexArray(6, new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.build());
		gaussBlur = GaussBlur.getInstance();

		screenTexture = new Texture(GL_TEXTURE_2D, Game.getScreenWidth(), Game.getScreenHeight(),
				new int[]{GL_NEAREST, GL_NEAREST}, true,
				new int[]{GL_RGBA, GL_DEPTH_COMPONENT32},
				new int[]{GL_BGRA, GL_DEPTH_COMPONENT},
				new int[]{GL_COLOR_ATTACHMENT0_EXT, GL_DEPTH_ATTACHMENT_EXT});
	}

	@Override
	public void render() {
		Camera camera = Camera.getMainCamera();
		Matrix4 viewProjection = camera.getViewProjection();

		screenTexture.bindAsRenderTarget();

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		ambientPass(viewProjection);

		lightingPass(viewProjection);

		applyFilter(FXAAFilter.getInstance(), screenTexture, null);

		/*
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
		*/
	}

	private void ambientPass(Matrix4 viewProjection) {
		renderState.setViewProjection(viewProjection);
		renderState.setAmbientIntenstiy(ambientLight);

		forwardAmbient.bind();

		for (Renderer renderer : renderers) {
			renderState.setRenderer(renderer);

			forwardAmbient.updateUniforms(renderState);

			renderer.render();
		}

		forwardAmbient.release();
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

			screenTexture.bindAsRenderTarget();

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);

			renderState.setViewProjection(viewProjection);
			renderState.setLight(light);
			renderState.setShadowMap(shadowMap);

			Shader shader = null;

			if (light instanceof DirectionalLight) {
				shader = forwardDirectional;
			} else if (light instanceof SpotLight) {
				shader = forwardSpot;
			}

			for (Renderer renderer : renderers) {
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
	}

	private void shadowPass(Light light) {
		Matrix4 lightViewProjection = light.getShadowViewProjection(Camera.getMainCamera().getTransform());

		renderState.setViewProjection(lightViewProjection);

		glEnable(GL_DEPTH_CLAMP);
		glCullFace(GL_FRONT);

		shadowMapGenerator.bind();
		for (Renderer renderer : renderers) {
			renderState.setRenderer(renderer);

			shadowMapGenerator.updateUniforms(renderState);

			renderer.render();
		}
		shadowMapGenerator.release();

		glCullFace(GL_BACK);
		glDisable(GL_DEPTH_CLAMP);

		if (light.getShadowSoftness() != 0.0f) {
			blurShadowMap(light.getShadowSoftness());
		}

		renderState.setLightViewProjection(biasMatrix.mul(lightViewProjection));
	}

	private void applyFilter(Filter filter, Texture source, Texture destination) {
		if (source == destination) {
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

		source.release();
		filter.release();

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private void blurShadowMap(float amount) {
		gaussBlur.setBlurScale(new Vector3(amount / shadowMap.getWidth(), 0.0f, 0.0f));

		applyFilter(gaussBlur, shadowMap, shadowMapTempTarget);

		gaussBlur.setBlurScale(new Vector3(0.0f, amount / shadowMap.getHeight(), 0.0f));

		applyFilter(gaussBlur, shadowMapTempTarget, shadowMap);
	}

	@Override
	public void dispose() {
		shadowMap.dispose();
		forwardAmbient.dispose();
		forwardDirectional.dispose();
		forwardPoint.dispose();
		forwardSpot.dispose();
		shadowMapTempTarget.dispose();
		screenTexture.dispose();
		shadowMapGenerator.dispose();
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

}
