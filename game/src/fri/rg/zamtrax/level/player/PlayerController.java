package fri.rg.zamtrax.level.player;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.CharacterController;
import zamtrax.components.Transform;

public class PlayerController extends Component {

	private static final float speed = 10.0f;
	private static final float jumpSpeed = 6.0f;
	private static final float mouseSensitivity = 0.25f;

	private Transform transform;
	private Transform cameraTransform;
	private CharacterController characterController;

	@Override
	public void onAdd() {
		transform = getTransform();
		cameraTransform = Camera.getMainCamera().getTransform();

		characterController = getGameObject().getComponent(CharacterController.class);
		characterController.warp(new Vector3(0.0f, 5.0f, 0.0f));
	}

	@Override
	public void update(float delta) {
		Vector2 mouseDelta = Input.getMouseDelta();

		if (mouseDelta.x != 0.0f) {
			cameraTransform.rotate(0.0f, mouseDelta.x * mouseSensitivity * delta, 0.0f);
		}

		if (mouseDelta.y != 0.0f) {
			cameraTransform.rotate(cameraTransform.right(), mouseDelta.y * mouseSensitivity * delta);
		}

		Vector3 motion = new Vector3();

		float d = speed * delta;

		if (Input.getKey(Input.KEY_W)) {
			motion = motion.add(cameraTransform.forward().mul(d));
		}

		if (Input.getKey(Input.KEY_S)) {
			motion = motion.add(cameraTransform.back().mul(d));
		}

		if (Input.getKey(Input.KEY_A)) {
			motion = motion.add(cameraTransform.left().mul(d));
		}

		if (Input.getKey(Input.KEY_D)) {
			motion = motion.add(cameraTransform.right().mul(d));
		}

		if (Input.getKey(Input.KEY_SPACE)) {
			characterController.jump();
		}

		characterController.move(motion);
	}

}
