import zamtrax.*;

public class FPSController extends SceneComponent {

	private final float speed = 10.0f;

	private Transform transform;

	@Override
	public void onCreate() {
		transform = getTransform();
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void update() {
		if (!Input.isMouseLocked()) {
			if (Input.getMouseButton(Input.MOUSE_BUTTON_1)) {
				Input.setMouseLocked(true);
			}

			return;
		}

		Vector2 mouseDelta = Input.getMouseDelta();

		if (mouseDelta.x != 0.0f) {
			transform.rotate(Vector3.UP, mouseDelta.x * 5.0f * Time.getDeltaTime());
		}

		if (mouseDelta.y != 0.0f) {
			transform.rotate(transform.getRotation().right(), mouseDelta.y * 5.0f * Time.getDeltaTime());
		}

		float d = speed * Time.getDeltaTime();

		if (Input.getKey(Input.KEY_W)) {
			transform.translate(transform.getRotation().forward().mul(d));
		}

		if (Input.getKey(Input.KEY_S)) {
			transform.translate(transform.getRotation().back().mul(d));
		}

		if (Input.getKey(Input.KEY_A)) {
			transform.translate(transform.getRotation().left().mul(d));
		}

		if (Input.getKey(Input.KEY_D)) {
			transform.translate(transform.getRotation().right().mul(d));
		}

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setMouseLocked(false);
		}
	}

}
