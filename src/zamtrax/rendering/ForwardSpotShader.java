package zamtrax.rendering;

import zamtrax.components.SpotLight;
import zamtrax.resources.Shader;
import zamtrax.*;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.Renderer;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

public class ForwardSpotShader extends Shader {

	private static ForwardSpotShader instance;

	private ForwardSpotShader() {
	}

	public static ForwardSpotShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forwardSpot.vs", ForwardSpotShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forwardSpot.fs", ForwardSpotShader.class.getClassLoader());

			BindingInfo bindingInfo = new BindingInfo.Builder()
					.bind(AttributeType.POSITION, 0, "position")
					.bind(AttributeType.UV, 1, "uv")
					.bind(AttributeType.NORMAL, 2, "normal")
					.build();

			List<Uniform> uniforms = new ArrayList<>();

			uniforms.add(new Uniform("M"));
			uniforms.add(new Uniform("MVP"));
			uniforms.add(new Uniform("material.shininess"));
			uniforms.add(new Uniform("material.specularIntensity"));
			uniforms.add(new Uniform("diffuse"));
			uniforms.add(new Uniform("shadowMap"));
			uniforms.add(new Uniform("cookie"));
			uniforms.add(new Uniform("cookieScale"));

			uniforms.add(new Uniform("spotLight.base.base.color"));
			uniforms.add(new Uniform("spotLight.base.base.intensity"));
			uniforms.add(new Uniform("spotLight.base.range"));
			uniforms.add(new Uniform("spotLight.base.position"));
			uniforms.add(new Uniform("spotLight.direction"));
			uniforms.add(new Uniform("spotLight.cutoff"));

			uniforms.add(new Uniform("modelLightViewProjection"));
			uniforms.add(new Uniform("shadowVarianceMin"));
			uniforms.add(new Uniform("lightBleed"));
			uniforms.add(new Uniform("castShadows"));
			uniforms.add(new Uniform("useCookie"));

			instance = new ForwardSpotShader();
			instance.init(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(RenderState renderState) {
		Matrix4 viewProjection = renderState.getViewProjection();
		Renderer renderer = renderState.getRenderer();
		Material material = renderer.getMaterial();
		SpotLight spotLight = (SpotLight) renderState.getLight();
		Matrix4 lightViewProjection = renderState.getLightViewProjection();
		Texture shadowMap = renderState.getShadowMap();

		Matrix4 model = renderer.getTransform().getLocalToWorldMatrix();
		Matrix4 mvp = viewProjection.mul(model);

		setUniform("M", model);
		setUniform("MVP", mvp);
		setUniform("material.shininess", material.getShininess());
		setUniform("material.specularIntensity", material.getSpecularIntensity());

		setUniform("diffuse", 0);
		material.getDiffuse().bind(0);

		if (spotLight.getCookie() != null) {
			setUniform("useCookie", true);
			setUniform("cookie", 1);
			spotLight.getCookie().bind(1);
			setUniform("cookieScale", spotLight.getCookieScale());
		}

		setUniform("spotLight.base.base.color", spotLight.getColor());
		setUniform("spotLight.base.base.intensity", spotLight.getIntensity());
		setUniform("spotLight.base.range", spotLight.getRange());
		setUniform("spotLight.base.position", spotLight.getTransform().getPosition());
		setUniform("spotLight.direction", spotLight.getTransform().forward());
		setUniform("spotLight.cutoff", spotLight.getCutoff());

		if (spotLight.getShadows() == Light.Shadows.HARD || spotLight.getCookie() != null) {
			setUniform("castShadows", spotLight.getShadows() == Light.Shadows.HARD);
			setUniform("shadowMap", 2);
			shadowMap.bind(2);

			setUniform("modelLightViewProjection", lightViewProjection.mul(model));
			setUniform("shadowVarianceMin", spotLight.getMinVariance());
			setUniform("lightBleed", spotLight.getLightBleed());
		}
	}

}
