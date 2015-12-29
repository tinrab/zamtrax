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
	}

}
