package fri.rg.zamtrax;

import zamtrax.SceneComponent;
import zamtrax.SceneObject;
import zamtrax.Time;

public class DestroyOnDelay extends SceneComponent {

	private float delay;
	private float timer;

	@Override
	public void update() {
		timer += Time.getDeltaTime();

		if (timer >= delay) {
			getObject().destroy();
		}
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

}
