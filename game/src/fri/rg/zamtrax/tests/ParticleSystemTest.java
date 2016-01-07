package fri.rg.zamtrax.tests;

import fri.rg.zamtrax.level.player.FreeLook;
import zamtrax.*;
import zamtrax.components.ParticleSystem;

public class ParticleSystemTest extends Scene {

	@Override
	public void onEnter() {
		super.onEnter();

		Transform fl = GameObject.create().addComponent(FreeLook.class).getTransform();
		fl.setPosition(new Vector3(0, 5, 0));
		fl.setRotation(Quaternion.fromEuler(new Vector3(30, 0, 0).mul(Mathf.DEG_TO_RAD)));


		{
			ParticleSystem ps = GameObject.create().addComponent(ParticleSystem.class);

			ps.getGameObject().addComponent(Particles.class);
		}
	}

}
