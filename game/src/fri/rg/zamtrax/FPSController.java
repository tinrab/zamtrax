package fri.rg.zamtrax;

import zamtrax.Component;
import zamtrax.Input;
import zamtrax.Transform;
import zamtrax.Vector2;

public class FPSController extends Component {

	private final float speed = 10.0f;
	private final float mouseSensitivity = 0.25f;

	private Transform transform;

	@Override
	public void onAdd() {
		transform = getTransform();
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void update(float delta) {
		if (!Input.isMouseLocked()) {
			if (Input.getMouseButton(Input.MOUSE_BUTTON_1)) {
				Input.setMouseLocked(true);
			}

			return;
		}

		Vector2 mouseDelta = Input.getMouseDelta();

		if (mouseDelta.x != 0.0f) {
			transform.rotate(0.0f, mouseDelta.x * mouseSensitivity * delta, 0.0f);
		}

		if (mouseDelta.y != 0.0f) {
			transform.rotate(transform.right(), mouseDelta.y * mouseSensitivity * delta);
		}

		float d = speed * delta;

		if (Input.getKey(Input.KEY_W)) {
			transform.translate(transform.forward().mul(d));
		}

		if (Input.getKey(Input.KEY_S)) {
			transform.translate(transform.back().mul(d));
		}

		if (Input.getKey(Input.KEY_A)) {
			transform.translate(transform.left().mul(d));
		}

		if (Input.getKey(Input.KEY_D)) {
			transform.translate(transform.right().mul(d));
		}

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setMouseLocked(false);
		}

	}

}
