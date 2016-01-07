package fri.rg.zamtrax.menu;

import zamtrax.*;

public class Oscillate extends Component {

	private Transform transform;
	private Vector3 startPosition;
	private float delay;

	@Override
	public void onAdd() {
		super.onAdd();

		transform = getTransform();
		startPosition = transform.getPosition();
	}

	@Override
	public void update(float delta) {
		float dy = Mathf.fastSin((float) Time.getInstance().currentSeconds() + delay) * 5.0f;

		transform.setPosition(startPosition.add(0.0f, dy, 0.0f));
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

}
