package fri.rg.zamtrax;

import zamtrax.Component;
import zamtrax.Time;

public class DestroyOnDelay extends Component {

	private float delay;
	private float timer;

	@Override
	public void update(float delta) {
		timer += delta;

		if (timer >= delay) {
			getGameObject().destroy();
		}
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

}
