package fri.rg.zamtrax.level.player;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.Transform;

public class FreeLook extends Component {

	private static final float MOUSE_SENSITIVITY = 8.0f;
	private static final float SPEED = 80.0f;
	private static final float SMOOTHING = 0.2f;

	private Transform transform;
	private Vector3 motion;

	@Override
	public void onAdd() {
		Camera camera = GameObject.create(getGameObject()).addComponent(Camera.class);
		Camera.setMainCamera(camera);

		camera.setProjection(Matrix4.createPerspective(60.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 500.0f));

		transform = getTransform();
		motion = new Vector3();

		Input.setMouseLocked(true);
	}

	@Override
	public void update(float delta) {
		Vector2 mouseDelta = Input.getMouseDelta();

		if (Input.isMouseLocked()) {
			if (mouseDelta.x != 0.0f) {
				transform.rotate(0.0f, mouseDelta.x * MOUSE_SENSITIVITY * delta, 0.0f);
			}

			if (mouseDelta.y != 0.0f) {
				transform.rotate(transform.right(), mouseDelta.y * MOUSE_SENSITIVITY * delta);
			}
		}

		if (Input.getKey(Input.KEY_W)) {
			motion = motion.add(transform.forward().mul(delta));
		}

		if (Input.getKey(Input.KEY_S)) {
			motion = motion.add(transform.back().mul(delta));
		}

		if (Input.getKey(Input.KEY_A)) {
			motion = motion.add(transform.left().mul(delta));
		}

		if (Input.getKey(Input.KEY_D)) {
			motion = motion.add(transform.right().mul(delta));
		}

		transform.translate(motion.mul(delta * SPEED), Space.WORLD);
		motion = Vector3.lerp(motion, Vector3.ZERO, delta / SMOOTHING);

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setMouseLocked(!Input.isMouseLocked());
		}
	}

}
