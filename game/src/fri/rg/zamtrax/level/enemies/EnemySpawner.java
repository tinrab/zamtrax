package fri.rg.zamtrax.level.enemies;

import fri.rg.zamtrax.level.Level;
import zamtrax.Component;
import zamtrax.GameObject;
import zamtrax.Mathf;
import zamtrax.Resources;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
import zamtrax.components.RigidBody;
import zamtrax.components.SphereCollider;
import zamtrax.resources.Material;
import zamtrax.resources.Mesh;

public class EnemySpawner extends Component {

	private Level level;

	private Mesh droneMesh;
	private Mesh droneBarrelMesh;
	private Material droneMaterial;
	private float timer;
	private float cooldown;

	@Override
	public void onAdd() {
		super.onAdd();

		level = Level.getInstance();

		droneMesh = Resources.loadModel("models/drone.ply");
		droneBarrelMesh = Resources.loadModel("models/droneBarrel.ply");
		droneMaterial = new Material("shaders/vertexColor.vs", "shaders/vertexColor.fs");
	}

	@Override
	public void update(float delta) {
		cooldown -= delta;
		timer += delta;

		if(cooldown <= 0.0f){
			cooldown = 15.0f - Mathf.clamp(timer / 30.0f, 0, 5);

			spawn();
		}
	}

	private void spawn() {
		GameObject drone = GameObject.create();
		drone.setTag("drone");

		drone.getTransform().setPosition(level.getFreeLocation().add(0.0f, 0.5f, 0.0f));

		GameObject top = GameObject.create(drone);
		GameObject bottom = GameObject.create(drone);

		top.addComponent(MeshFilter.class).setMesh(droneBarrelMesh);
		top.addComponent(MeshRenderer.class).setMaterial(droneMaterial);

		bottom.addComponent(MeshFilter.class).setMesh(droneMesh);
		bottom.addComponent(MeshRenderer.class).setMaterial(droneMaterial);

		top.getTransform().setLocalPosition(0, 0, 0);
		bottom.getTransform().setLocalPosition(0, 0, 0);

		drone.addComponent(Drone.class);
		drone.addComponent(SphereCollider.class).setRadius(0.5f);
		drone.addComponent(RigidBody.class).setKinematic(true);
	}

}
