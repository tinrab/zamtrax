package zamtrax;

import zamtrax.resources.Material;

public abstract class Renderer extends Component {

	protected Material material;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;

		RenderModule.getInstance().consolidate();
	}

	abstract void render();

}
