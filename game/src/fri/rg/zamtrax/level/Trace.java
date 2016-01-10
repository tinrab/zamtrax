package fri.rg.zamtrax.level;

import zamtrax.*;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
import zamtrax.components.ParticleSystem;
import zamtrax.components.PointLight;
import zamtrax.rendering.RenderState;
import zamtrax.resources.Material;
import zamtrax.resources.Mesh;

public class Trace extends Component {

	private PointLight pointLight;
	private ParticleSystem particleSystem;

	@Override
	public void onAdd() {
		super.onAdd();

		Material traceMaterial = new Material("shaders/trace.vs", "shaders/trace.fs");
		Mesh traceMesh = Resources.loadModel("models/trace.ply");

		addComponent(MeshFilter.class).setMesh(traceMesh);
		addComponent(MeshRenderer.class).setMaterial(traceMaterial);

		pointLight = GameObject.create(getGameObject()).addComponent(PointLight.class);
		pointLight.setColor(new Color(1.0f, 0.38f, 0.0f));

		particleSystem = GameObject.create().addComponent(ParticleSystem.class);
		particleSystem.setGravity(new Vector3(0, -9.81f, 0.0f));
		particleSystem.setStartColor(new Color(1.0f, 0.4f, 0.0f));
		particleSystem.setEndColor(new Color(0.5f, 0.0f, 0.0f));
	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	public void setHit(RaycastHit hit) {
		if (hit == null) {
			transform.setScale(1, 1, 1000000);
		} else {
			transform.setScale(1, 1, hit.getDistance());

			pointLight.getTransform().setPosition(hit.getPoint().add(hit.getNormal()));

			for (int i = 0; i < 30; i++) {
				ParticleSystem.Particle p = new ParticleSystem.Particle();

				p.setPosition(hit.getPoint().add(new Vector3(Random.randomFloat() - 0.5f, Random.randomFloat() - 0.5f, Random.randomFloat() - 0.5f).mul(0.2f)));
				p.setVelocity(new Vector3(Random.randomFloat() - 0.5f, Random.randomFloat() - 0.5f, Random.randomFloat() - 0.5f).mul(5f));
				p.setLifetime(0.25f);
				p.setScale(Random.randomFloat() * 0.1f + 0.05f);
				p.setGravity(Random.randomFloat());

				particleSystem.emit(p);
			}
		}
	}

}
