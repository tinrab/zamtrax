package zamtrax.rendering;

import zamtrax.Color;
import zamtrax.Matrix4;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.Renderer;
import zamtrax.resources.Texture;

public class RenderState {

	private Renderer renderer;
	private Matrix4 projection;
	private Matrix4 viewProjection;
	private Color ambientIntenstiy;
	private Light light;
	private Matrix4 lightViewProjection;
	private Texture shadowMap;

	public void clear() {
		projection = null;
		renderer = null;
		viewProjection = null;
		ambientIntenstiy = null;
		light = null;
		lightViewProjection = null;
		shadowMap = null;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public Matrix4 getViewProjection() {
		return viewProjection;
	}

	public void setViewProjection(Matrix4 viewProjection) {
		this.viewProjection = viewProjection;
	}

	public Matrix4 getProjection() {
		return projection;
	}

	public void setProjection(Matrix4 projection) {
		this.projection = projection;
	}

	public Color getAmbientIntenstiy() {
		return ambientIntenstiy;
	}

	public void setAmbientIntenstiy(Color ambientIntenstiy) {
		this.ambientIntenstiy = ambientIntenstiy;
	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}

	public Matrix4 getLightViewProjection() {
		return lightViewProjection;
	}

	public void setLightViewProjection(Matrix4 lightViewProjection) {
		this.lightViewProjection = lightViewProjection;
	}

	public Texture getShadowMap() {
		return shadowMap;
	}

	public void setShadowMap(Texture shadowMap) {
		this.shadowMap = shadowMap;
	}

}
