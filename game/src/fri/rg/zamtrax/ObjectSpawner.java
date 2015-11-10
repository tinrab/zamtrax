package fri.rg.zamtrax;

import zamtrax.*;
import zamtrax.resources.Material;
import zamtrax.resources.Mesh;

public class ObjectSpawner extends SceneComponent {

	private Mesh mesh;
	private Material material;

	private float timer;

	@Override
	public void update() {
		timer -= Time.getDeltaTime();

		if (timer <= 0.0f) {
			timer = Random.randomFloat() * 0.2f;

			for (int i = 0; i < Random.randomInteger(2); i++) {
				spawn();
			}
		}
	}

	private void spawn() {
		SceneObject cube = SceneObject.create();

		cube.addComponent(MeshFilter.class).setMesh(mesh);
		cube.addComponent(MeshRenderer.class).setMaterial(material);

		cube.getTransform().translate(Random.randomFloat() * 5.0f - 2.5f, 5.0f, Random.randomFloat() * 5.0f - 2.5f);

		cube.addComponent(BoxCollider.class);
		cube.addComponent(RigidBody.class).setMass(1.0f);
		cube.addComponent(DestroyOnDelay.class).setDelay(7.0f);
	}

	public void setResources(Mesh mesh, Material material) {
		this.mesh = mesh;
		this.material = material;
	}

}
