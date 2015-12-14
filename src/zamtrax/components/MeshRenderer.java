package zamtrax.components;

import zamtrax.RequireComponent;

@RequireComponent(components = {MeshFilter.class})
public final class MeshRenderer extends Renderer {

	private MeshFilter meshFilter;

	@Override
	public void onAdd() {
		super.onAdd();

		meshFilter = getGameObject().getComponent(MeshFilter.class);
	}

	@Override
	public void render() {
		meshFilter.getMesh().render();
		/*
		Matrix4 modelView = transform.getLocalToWorldMatrix();

		material.bind();

		Shader shader = material.getShader();

		Matrix3 normalMatrix = modelView.toMatrix3().invert().transpose();

		shader.setUniform("projectionMatrix", projection);
		shader.setUniform("modelViewMatrix", modelView);
		shader.setUniform("normalMatrix", normalMatrix);

		meshFilter.getMesh().render();

		material.unbind();
		*/
	}

}
