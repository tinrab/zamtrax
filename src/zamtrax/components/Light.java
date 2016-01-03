package zamtrax.components;

import zamtrax.*;
import zamtrax.resources.Texture;

public abstract class Light extends Component {

	public enum Shadows {
		NONE, HARD, SOFT
	}

	protected Color color;
	protected float intensity;
	protected Shadows shadows;
	protected Matrix4 shadowProjection;
	protected float shadowSoftness;
	protected float minVariance;
	protected float lightBleed;
	protected Texture cookie;
	protected float cookieScale;

	@Override
	public void onAdd() {
		color = Color.createWhite();
		intensity = 1.0f;
		shadowSoftness = 1.0f;
		minVariance = 0.002f;
		lightBleed = 0.1f;
		cookieScale = 1.0f;
		shadows = Shadows.NONE;
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

	public Matrix4 getShadowViewProjection() {
		Transform t = getTransform();

		Matrix4 r = t.getRotation().conjugate().toMatrix();
		Vector3 p = t.getPosition().mul(-1.0f);

		return shadowProjection.mul(r.mul(Matrix4.createTranslation(p)));
	}

}
