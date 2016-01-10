package fri.rg.zamtrax.tests;

import zamtrax.Color;
import zamtrax.Component;
import zamtrax.Input;
import zamtrax.components.ParticleSystem;

public class Particles extends Component {

	private ParticleSystem particleSystem;

	@Override
	public void onAdd() {
		super.onAdd();

		particleSystem = getComponent(ParticleSystem.class);
		particleSystem.setStartColor(Color.createWhite());
		particleSystem.setEndColor(Color.createBlue());

	}

	@Override
	public void update(float delta) {
		if (Input.getKey(Input.KEY_F)) {
			for (int i = 0; i < 500; i++) {
				ParticleSystem.Particle p = new ParticleSystem.Particle();

				particleSystem.emit(p);
			}
		}
	}

}
