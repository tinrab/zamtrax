package zamtrax.components;

import zamtrax.Color;
import zamtrax.Component;
import zamtrax.Matrix4;
import zamtrax.resources.Texture;

public abstract class Light extends Component {

	public enum Shadows {
		NONE, HARD, SOFT
	}

	private Color color;
	private float intensity;
	private Shadows shadows;
	private Matrix4 shadowProjection;
	private float shadowSoftness;
	private float minVariance;
	private float lightBleed;
	private Texture cookie;
	private float cookieScale;

	@Override
	public void onAdd() {
		color = Color.createWhite();
		intensity = 1.0f;
		shadowSoftness = 1.0f;
		minVariance = 0.00002f;
		lightBleed = 0.4f;
		cookieScale = 1.0f;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Shadows getShadows() {
		return shadows;
	}

	public void setShadows(Shadows shadows) {
		this.shadows = shadows;
	}

	public Matrix4 getShadowProjection() {
		return shadowProjection;
	}

	protected void setShadowProjection(Matrix4 shadowProjection) {
		this.shadowProjection = shadowProjection;
	}

	public float getLightBleed() {
		return lightBleed;
	}

	public void setLightBleed(float lightBleed) {
		this.lightBleed = lightBleed;
	}

	public float getMinVariance() {
		return minVariance;
	}

	public void setMinVariance(float minVariance) {
		this.minVariance = minVariance;
	}

	public float getShadowSoftness() {
		return shadowSoftness;
	}

	public void setShadowSoftness(float shadowSoftness) {
		this.shadowSoftness = shadowSoftness;
	}

	public void setCookie(Texture cookie) {
		this.cookie = cookie;
	}

	public Texture getCookie() {
		return cookie;
	}

	public void setCookieScale(float cookieScale) {
		this.cookieScale = cookieScale;
	}

	public float getCookieScale() {
		return cookieScale;
	}

}
