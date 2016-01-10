package fri.rg.zamtrax.menu;

import zamtrax.*;
import zamtrax.components.ParticleSystem;

@RequireComponent(components = {ParticleSystem.class})
public class ParticleEmitter extends Component {

	private ParticleSystem particleSystem;
	private Vector3 position;
	private float timer;

	@Override
	public void onAdd() {
		super.onAdd();

		particleSystem = getComponent(ParticleSystem.class);
		particleSystem.setGravity(new Vector3(0, 0, -3));
		particleSystem.setStartColor(new Color(0, 0, 0, 0));
		particleSystem.setEndColor(new Color(1, 1, 1, 0.5f));
		particleSystem.setRotationSpeed(20.0f);
	}

	@Override
	public void update(float delta) {
		position = transform.getPosition();

		timer -= delta;

		if (timer <= 0.0f) {
			timer = 0.1f;

			for (int i = 0; i < 50; i++) {
				particleSystem.emit(spaw());
			}
		}
	}

	private ParticleSystem.Particle spaw() {
		ParticleSystem.Particle p = new ParticleSystem.Particle();

		p.setLifetime(7.0f);
		p.setPosition(position.add(Random.randomFloat() * 20 - 10, Random.randomFloat() * 10 - 5, 10));
		p.setScale(Random.randomFloat() * 0.03f + 0.005f);
		p.setGravity(Random.randomFloat() * 0.8f + 0.2f);

		return p;
	}

}
