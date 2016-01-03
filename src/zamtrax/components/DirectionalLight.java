package zamtrax.components;

import zamtrax.*;

public class DirectionalLight extends Light {

	private float halfShadowAreaSize;

	@Override
	public void onAdd() {
		super.onAdd();

		setShadowAreaSize(50.0f);
	}

	public float getShadowAreaSize() {
		return halfShadowAreaSize * 2.0f;
	}

	public void setShadowAreaSize(float shadowAreaSize) {
		halfShadowAreaSize = shadowAreaSize / 2.0f;

		setShadowProjection(Matrix4.createOrthographic(-halfShadowAreaSize, halfShadowAreaSize, -halfShadowAreaSize, halfShadowAreaSize, -halfShadowAreaSize, halfShadowAreaSize));
	}

	@Override
	public Matrix4 getShadowViewProjection() {
		Transform mainCamera = Camera.getMainCamera().getTransform();
		Vector3 p = mainCamera.getPosition();

		// TODO refactoring
		final int shadowMapSize = 1024;
		float texelSize = (halfShadowAreaSize * 2.0f) / (float) shadowMapSize;

		Vector3 lightSpaceCamera = p.rotate(getTransform().getRotation().conjugate());

		lightSpaceCamera.x = texelSize * Mathf.floor(lightSpaceCamera.x / texelSize);
		lightSpaceCamera.y = texelSize * Mathf.floor(lightSpaceCamera.y / texelSize);

		p = lightSpaceCamera.rotate(getTransform().getRotation());

		return calcVP(p, getTransform().getRotation());
	}

	private Matrix4 calcVP(Vector3 position, Quaternion rotation) {
		Vector3 p = position.mul(-1.0f);
		Matrix4 r = rotation.conjugate().toMatrix();

		return shadowProjection.mul(r.mul(Matrix4.createTranslation(p)));
	}

}
