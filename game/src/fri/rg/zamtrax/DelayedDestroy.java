package fri.rg.zamtrax;

import zamtrax.Component;

public class DelayedDestroy extends Component {

	private float timer = 5.0f;

	@Override
	public void update(float delta) {
		timer -= delta;

		if (timer <= 0.0f) {
			getGameObject().destroy();
		}
	}

	public void setDelay(float delay) {
		timer = delay;
	}

}
