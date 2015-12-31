package zamtrax.rendering;

import zamtrax.*;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.Renderer;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

public class ForwardDirectionalShader extends Shader {

	private static ForwardDirectionalShader instance;

	private ForwardDirectionalShader() {
	}

	public static ForwardDirectionalShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forwardDirectional.vs", ForwardDirectionalShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forwardDirectional.fs", ForwardDirectionalShader.class.getClassLoader());

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

			uniforms.add(new Uniform("directionalLight.base.color"));
			uniforms.add(new Uniform("directionalLight.base.intensity"));
			uniforms.add(new Uniform("directionalLight.direction"));

			uniforms.add(new Uniform("modelLightViewProjection"));
			uniforms.add(new Uniform("shadowVarianceMin"));
			uniforms.add(new Uniform("lightBleed"));

			instance = new ForwardDirectionalShader();
			instance.init(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(RenderState renderState) {
		Matrix4 viewProjection = renderState.getViewProjection();
		Renderer renderer = renderState.getRenderer();
		Material material = renderer.getMaterial();
		DirectionalLight directionalLight = (DirectionalLight) renderState.getLight();
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

		if (directionalLight.getCookie() != null) {
			setUniform("cookie", 1);
			directionalLight.getCookie().bind(1);
			setUniform("cookieScale", directionalLight.getCookieScale());
		}

		setUniform("directionalLight.base.color", directionalLight.getColor());
		setUniform("directionalLight.base.intensity", directionalLight.getIntensity());
		setUniform("directionalLight.direction", directionalLight.getTransform().forward());

		if (directionalLight.getShadows() == Light.Shadows.HARD) {
			setUniform("shadowMap", 2);
			shadowMap.bind(2);

			setUniform("modelLightViewProjection", lightViewProjection.mul(model));
			setUniform("shadowVarianceMin", directionalLight.getMinVariance());
			setUniform("lightBleed", directionalLight.getLightBleed());
		}
	}

}
