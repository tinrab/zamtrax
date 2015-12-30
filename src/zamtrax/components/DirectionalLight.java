package zamtrax.components;

import zamtrax.Matrix4;

public class DirectionalLight extends Light {

	@Override
	public void onAdd() {
		super.onAdd();

		setShadows(Shadows.HARD);
		setShadowProjection(Matrix4.createOrthographic(-40, 40, -40, 40, -40, 40));
	}

}
