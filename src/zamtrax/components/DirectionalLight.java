package zamtrax.components;

import zamtrax.Matrix4;

public class DirectionalLight extends Light {

	@Override
	public void onAdd() {
		super.onAdd();

		setShadowProjection(Matrix4.createOrthographic(-50, 50, -50, 50, -50, 50));
	}

}
