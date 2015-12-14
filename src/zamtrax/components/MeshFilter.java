package zamtrax.components;

import zamtrax.Component;
import zamtrax.resources.Mesh;

public final class MeshFilter extends Component {

	private Mesh mesh;

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
}
