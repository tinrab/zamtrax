package fri.rg.zamtrax.level.player;

import zamtrax.*;

public class PlayerController extends Component {

	private static final float speed = 10.0f;
	private static final float jumpSpeed = 6.0f;
	private static final float mouseSensitivity = 0.25f;

	private Transform transform;
	private Camera camera;
	private CharacterController characterController;

	@Override
	public void onAdd() {
		transform = getTransform();
		camera = Camera.getMainCamera();

		characterController = getGameObject().getComponent(CharacterController.class);
		characterController.warp(new Vector3(0.0f, 5.0f, 0.0f));
	}

	@Override
	public void update(float delta) {
		Vector2 mouseDelta = Input.getMouseDelta();

		if (mouseDelta.x != 0.0f) {
			transform.rotate(0.0f, mouseDelta.x * mouseSensitivity * delta, 0.0f);
		}

		if (mouseDelta.y != 0.0f) {
			transform.rotate(transform.right(), mouseDelta.y * mouseSensitivity * delta);
		}

		Vector3 motion = new Vector3();

		float d = speed * delta;

		if (Input.getKey(Input.KEY_W)) {
			motion = motion.add(transform.forward().mul(d));
		}

		if (Input.getKey(Input.KEY_S)) {
			motion = motion.add(transform.back().mul(d));
		}

		if (Input.getKey(Input.KEY_A)) {
			motion = motion.add(transform.left().mul(d));
		}

		if (Input.getKey(Input.KEY_D)) {
			motion = motion.add(transform.right().mul(d));
		}

		if (Input.getKey(Input.KEY_SPACE)) {
			characterController.jump();
		}

		characterController.move(motion);
	}

}
