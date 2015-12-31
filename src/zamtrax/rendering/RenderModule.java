package zamtrax.rendering;

import zamtrax.*;
import zamtrax.components.*;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.*;

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
		glEnable(GL_DEPTH_CLAMP);

		forwardDirectional = ForwardDirectionalShader.getInstance();
		forwardAmbient = ForwardAmbientShader.getInstance();
		forwardSpot = ForwardSpotShader.getInstance();
		forwardPoint = ForwardPointShader.getInstance();

		shadowMap = new Texture(GL_TEXTURE_2D, 1024, 1024, GL_LINEAR_MIPMAP_LINEAR, true, GL_RGB16, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);
		shadowMapTempTarget = new Texture(GL_TEXTURE_2D, 1024, 1024, GL_LINEAR_MIPMAP_LINEAR, true, GL_RGB16, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);

		shadowMapGenerator = ShadowMapGeneratorShader.getInstance();
		biasMatrix = Matrix4.createScale(0.5f, 0.5f, 0.5f).mul(Matrix4.createTranslation(1.0f, 1.0f, 1.0f));

		filterVertexArray = new VertexArray(12, new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.build());
		gaussBlur = GaussBlur.getInstance();
	}

	@Override
	public void render() {
		Camera camera = Camera.getMainCamera();
		Camera.ClearFlags clearFlag = camera.getClearFlags();
		Matrix4 viewProjection = camera.getViewProjection();

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		ambientPass(viewProjection);

		lightingPass(viewProjection);

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
			glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			if (light.getShadows() == Light.Shadows.HARD) {
				shadowPass(light);
			}

			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

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
			} else {
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
		Matrix4 shadowProjection = light.getShadowProjection();
		Matrix4 r = light.getTransform().getRotation().conjugate().toMatrix();
		Vector3 p = light.getTransform().getPosition().mul(-1.0f);
		Matrix4 lightViewProjection = shadowProjection.mul(r.mul(Matrix4.createTranslation(p)));

		renderState.setViewProjection(lightViewProjection);

		glCullFace(GL_FRONT);

		shadowMapGenerator.bind();
		for (Renderer renderer : renderers) {
			renderState.setRenderer(renderer);

			shadowMapGenerator.updateUniforms(renderState);

			renderer.render();
		}
		shadowMapGenerator.release();

		glCullFace(GL_BACK);

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

		glClear(GL_DEPTH_BUFFER_BIT);

		filter.bind();

		filter.updateUniforms(renderState);

		source.bind(0);

		filterVertexArray.clear();

		filterVertexArray.put(-1, -1, 0, 1).put(0, 0);
		filterVertexArray.put(-1, 1, 0, 1).put(0, 1);
		filterVertexArray.put(1, 1, 0, 1).put(1, 1);

		filterVertexArray.put(-1, -1, 0, 1).put(0, 0);
		filterVertexArray.put(1, 1, 0, 1).put(1, 1);
		filterVertexArray.put(1, -1, 0, 1).put(1, 0);

		filterVertexArray.render(GL_TRIANGLES, 0, 6);

		source.release();

		filter.release();
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
