package zamtrax;

import zamtrax.SceneComponent;
import zamtrax.resources.Mesh;

public final class MeshFilter extends SceneComponent {

	private Mesh mesh;

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
}
