package zamtrax.rendering;

import zamtrax.components.*;
import zamtrax.resources.Shader;
import zamtrax.resources.Shader;
import zamtrax.*;
import zamtrax.resources.*;

import java.util.ArrayList;
import java.util.List;

public class ForwardPointShader extends Shader {

	private static ForwardPointShader instance;

	private ForwardPointShader() {
	}

	public static ForwardPointShader getInstance() {
		if (instance == null) {
			String vs = Resources.loadText("shaders/forwardPoint.vs", ForwardPointShader.class.getClassLoader());
			String fs = Resources.loadText("shaders/forwardPoint.fs", ForwardPointShader.class.getClassLoader());

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

			uniforms.add(new Uniform("pointLight.base.color"));
			uniforms.add(new Uniform("pointLight.base.intensity"));
			uniforms.add(new Uniform("pointLight.range"));
			uniforms.add(new Uniform("pointLight.position"));

			uniforms.add(new Uniform("modelLightViewProjection"));
			uniforms.add(new Uniform("shadowVarianceMin"));
			uniforms.add(new Uniform("lightBleed"));

			instance = new ForwardPointShader();
			instance.init(vs, fs, bindingInfo, uniforms);
		}

		return instance;
	}

	@Override
	public void updateUniforms(RenderState renderState) {
		Matrix4 viewProjection = renderState.getViewProjection();
		Renderer renderer = renderState.getRenderer();
		Material material = renderer.getMaterial();
		PointLight pointLight = (PointLight) renderState.getLight();
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

		if (pointLight.getCookie() != null) {
			setUniform("cookie", 1);
			pointLight.getCookie().bind(1);
			setUniform("cookieScale", pointLight.getCookieScale());
		}

		setUniform("pointLight.base.color", pointLight.getColor());
		setUniform("pointLight.base.intensity", pointLight.getIntensity());
		setUniform("pointLight.range", pointLight.getRange());
		setUniform("pointLight.position", pointLight.getTransform().getPosition());

		if (pointLight.getShadows() == Light.Shadows.HARD) {
			setUniform("shadowMap", 2);
			shadowMap.bind(2);

			setUniform("modelLightViewProjection", lightViewProjection.mul(model));
			setUniform("shadowVarianceMin", pointLight.getMinVariance());
			setUniform("lightBleed", pointLight.getLightBleed());
		}
	}

}
