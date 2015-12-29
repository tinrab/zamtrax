package zamtrax.components;

import zamtrax.Component;
import zamtrax.RenderModule;
import zamtrax.resources.Material;
import zamtrax.resources.Shader;

public abstract class Renderer extends Component {

	protected Material material;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public abstract void render();

}
