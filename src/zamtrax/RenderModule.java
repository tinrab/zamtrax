package zamtrax;

import zamtrax.components.*;
import zamtrax.resources.*;
import zamtrax.ui.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

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

	private ForwardAmbientShader forwardAmbient;
	private ForwardDirectionalShader forwardDirectional;

	private FrameBuffer shadowMap;
	private ShadowMapGeneratorShader shadowMapGenerator;
	private Matrix4 biasMatrix;

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
	public void onCreate() {
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_DEPTH_CLAMP);

		forwardDirectional = ForwardDirectionalShader.getInstance();
		forwardAmbient = ForwardAmbientShader.getInstance();

		shadowMap = new FrameBuffer(GL_TEXTURE_2D, 1024, 1024, GL_NEAREST, true, GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT_EXT);
		shadowMapGenerator = ShadowMapGeneratorShader.getInstance();
		biasMatrix = Matrix4.createScale(0.5f, 0.5f, 0.5f).mul(Matrix4.createTranslation(1.0f, 1.0f, 1.0f));
	}

	@Override
	public void render() {
		Camera camera = Camera.getMainCamera();
		Camera.ClearFlags clearFlag = camera.getClearFlags();
		Matrix4 viewProjection = camera.getViewProjection();

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

		ambientPass(viewProjection);

		directionalPass(viewProjection);
	}

	private void ambientPass(Matrix4 viewProjection) {
		forwardAmbient.bind();

		for (Renderer renderer : renderers) {
			forwardAmbient.updateUniforms(renderer.getTransform(), viewProjection, renderer.getMaterial(), ambientLight);

			renderer.render();
		}

		forwardAmbient.release();
	}

	private void directionalPass(Matrix4 viewProjection) {
		for (DirectionalLight directionalLight : directionalLights) {
			shadowMap.bindAsRenderTarget();
			glClear(GL_DEPTH_BUFFER_BIT);

			Matrix4 lightViewProjection = null;

			if (directionalLight.getShadows() == Light.Shadows.HARD) {
				Matrix4 shadowProjection = directionalLight.getShadowProjection();
				Matrix4 r = directionalLight.getTransform().getRotation().conjugate().toMatrix();
				Vector3 p = directionalLight.getTransform().getPosition().mul(-1.0f);

				lightViewProjection = shadowProjection.mul(r.mul(Matrix4.createTranslation(p)));

				shadowMapGenerator.bind();
				for (Renderer renderer : renderers) {
					shadowMapGenerator.updateUniforms(renderer.getTransform(), lightViewProjection);

					renderer.render();
				}
				shadowMapGenerator.release();
			}

			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);

			lightViewProjection = biasMatrix.mul(lightViewProjection);

			forwardDirectional.bind();
			for (Renderer renderer : renderers) {
				forwardDirectional.updateUniforms(renderer, viewProjection, directionalLight, lightViewProjection);

				renderer.getMaterial().getDiffuse().bind(0);
				shadowMap.bind(0, 1);

				renderer.render();

				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, 0);
			}
			glBindTexture(GL_TEXTURE_2D, 0);
			forwardDirectional.release();

			glDepthFunc(GL_LESS);
			glDepthMask(true);
			glDisable(GL_BLEND);
		}
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
