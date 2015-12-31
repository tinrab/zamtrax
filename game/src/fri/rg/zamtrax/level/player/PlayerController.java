package fri.rg.zamtrax.level.player;

import zamtrax.Component;
import zamtrax.Input;
import zamtrax.Vector2;
import zamtrax.Vector3;
import zamtrax.components.Camera;
import zamtrax.components.CharacterController;
import zamtrax.Transform;

public class PlayerController extends Component {

	private static final float SPEED = 10.0f;
	private static final float JUMP_SPEED = 10.0f;
	private static final float MOUSE_SENSITIVITY = 0.25f;

	private Transform transform;
	private Transform cameraTransform;
	private CharacterController characterController;

	@Override
	public void onAdd() {
		transform = getTransform();
		cameraTransform = Camera.getMainCamera().getTransform();

		characterController = getGameObject().getComponent(CharacterController.class);
		characterController.setJumpSpeed(JUMP_SPEED);

		Input.setMouseLocked(true);
	}

	@Override
	public void update(float delta) {
		Vector2 mouseDelta = Input.getMouseDelta();

		if (mouseDelta.x != 0.0f) {
			cameraTransform.rotate(0.0f, mouseDelta.x * MOUSE_SENSITIVITY * delta, 0.0f);
		}

		if (mouseDelta.y != 0.0f) {
			cameraTransform.rotate(cameraTransform.right(), mouseDelta.y * MOUSE_SENSITIVITY * delta);
		}

		Vector3 motion = new Vector3();

		if (Input.getKey(Input.KEY_W)) {
			motion = motion.add(cameraTransform.forward());
		}

		if (Input.getKey(Input.KEY_S)) {
			motion = motion.add(cameraTransform.back());
		}

		if (Input.getKey(Input.KEY_A)) {
			motion = motion.add(cameraTransform.left());
		}

		if (Input.getKey(Input.KEY_D)) {
			motion = motion.add(cameraTransform.right());
		}

		if (Input.getKey(Input.KEY_SPACE)) {
			characterController.jump();
		}

		characterController.move(new Vector3(motion.x, 0.0f, motion.z).normalized().mul(SPEED * delta));

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setMouseLocked(!Input.isMouseLocked());
		}
	}

}
