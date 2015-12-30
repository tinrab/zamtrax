package fri.rg.zamtrax.level.player;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.Transform;

public class FreeLook extends Component {

	private static final float MOUSE_SENSITIVITY = 0.25f;
	private static final float SPEED = 10.0f;

	private Transform transform;

	@Override
	public void onAdd() {
		Camera camera = GameObject.create(getGameObject()).addComponent(Camera.class);
		Camera.setMainCamera(camera);

		camera.setProjection(Matrix4.createPerspective(60.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 500.0f));

		transform = getTransform();

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
			transform.translate(transform.forward().mul(delta * SPEED));
		}

		if (Input.getKey(Input.KEY_S)) {
			transform.translate(transform.back().mul(delta * SPEED));
		}

		if (Input.getKey(Input.KEY_A)) {
			transform.translate(transform.left().mul(delta * SPEED));
		}

		if (Input.getKey(Input.KEY_D)) {
			transform.translate(transform.right().mul(delta * SPEED));
		}

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setMouseLocked(!Input.isMouseLocked());
		}
	}

}
