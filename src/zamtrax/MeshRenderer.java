package zamtrax;

import zamtrax.resources.Material;
import zamtrax.resources.Shader;

public final class MeshRenderer extends Renderer {

	private MeshFilter meshFilter;
	private Material material;
	private Transform transform;

	@Override
	public void onAdd() {
		super.onAdd();

		transform = getTransform();
		meshFilter = getGameObject().getComponent(MeshFilter.class);
	}

	@Override
	void render(Matrix4 viewProjection) {
		Matrix4 modelView = transform.getLocalToWorldMatrix();

		material.bind();

		Shader shader = material.getShader();

		Matrix3 normalMatrix = modelView.toMatrix3().invert().transpose();

		shader.setUniform("projectionMatrix", viewProjection);
		shader.setUniform("modelViewMatrix", modelView);
		shader.setUniform("normalMatrix", normalMatrix);

		meshFilter.getMesh().render();

		material.unbind();
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

}
