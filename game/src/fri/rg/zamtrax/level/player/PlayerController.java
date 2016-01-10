package fri.rg.zamtrax.level.player;

import fri.rg.zamtrax.level.HUD;
import fri.rg.zamtrax.level.Trace;
import fri.rg.zamtrax.level.enemies.Drone;
import zamtrax.*;
import zamtrax.components.*;

public class PlayerController extends Component {

	private static final float SPEED = 5.0f;
	private static final float JUMP_SPEED = 6.0f;
	private static final float MOUSE_SENSITIVITY = 5.0f;

	private Transform transform;
	private Transform cameraTransform;
	private CharacterController characterController;
	private Transform gun;
	private Vector3 gunPosition;
	private float recoil = 0.20f;
	private PointLight flash;
	private float cooldown;
	private Trace trace;
	private boolean traceFade;

	private Vector3 restPosition;
	private final float transitionSpeed = 20.0f;
	private final float bobSpeed = 10.0f;
	private final float bobAmount = 0.1f;
	private float timer;

	@Override
	public void onAdd() {
		transform = getTransform();
		cameraTransform = Camera.getMainCamera().getTransform();

		gun = cameraTransform.getGameObject().getChildByIndex(0).getTransform();
		gunPosition = gun.getLocalPosition();

		characterController = getGameObject().getComponent(CharacterController.class);
		characterController.setJumpSpeed(JUMP_SPEED);

		cameraTransform.setLocalPosition(0.0f, characterController.getHeight() / 2.0f, 0.0f);
		restPosition = cameraTransform.getLocalPosition();

		flash = GameObject.create(cameraTransform.getGameObject()).addComponent(PointLight.class);
		flash.getTransform().setLocalPosition(0.1f, -0.17f, 0.25f);
		flash.setColor(new Color(1.0f, 0.9f, 0.7f));
		flash.setIntensity(0.0f);
		flash.setEnabled(false);

		trace = GameObject.create().addComponent(Trace.class);
		trace.getGameObject().setActive(false);

		Input.setMouseLocked(true);


		timer = Mathf.PI / 2.0f;
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

		motion = new Vector3(motion.x, 0.0f, motion.z).normalized().mul(SPEED * delta);
		characterController.move(motion);

		if ((motion.x != 0.0f || motion.z != 0.0f) && characterController.isGrounded()) {
			timer += bobSpeed * delta;
			Vector3 newPosition = new Vector3(Mathf.fastCos(timer) * bobAmount, restPosition.y + Mathf.abs(Mathf.fastSin(timer) * bobAmount), restPosition.z);
			cameraTransform.setLocalPosition(newPosition);
		} else {
			timer = Mathf.PI / 2.0f;
			cameraTransform.setLocalPosition(Vector3.lerp(cameraTransform.getLocalPosition(), restPosition, transitionSpeed * delta));
		}

		if (timer > Mathf.PI2) {
			timer = 0.0f;
		}

		cooldown -= delta;
		flash.setIntensity(flash.getIntensity() - delta * 40.0f);
		gun.setLocalPosition(Vector3.lerp(gun.getLocalPosition(), gunPosition, delta * 40.0f));

		if (traceFade) {
			trace.getGameObject().setActive(false);
		} else {
			traceFade = true;
		}

		if (flash.getIntensity() < 0.0f) {
			flash.setEnabled(false);
			flash.setIntensity(0.0f);
		}

		if (cooldown <= 0.0f && Input.getMouseButton(Input.MOUSE_BUTTON_1)) {
			cooldown = 0.15f;

			fire(gun.getPosition().add(motion));
		}
	}

	private void fire(Vector3 barrelPosition) {
		RaycastHit hit = Physics.getInstance().raycast(transform.getPosition(), cameraTransform.forward(), 40.0f);

		if (hit != null) {
			GameObject go = ((Component) hit.getHitObject()).getGameObject();

			if (go.compareTag("drone")) {
				float damage = Random.gauss() * 10.0f + 100.0f;

				go.getComponent(Drone.class).takeDamage(damage);
				HUD.getInstance().addFloatingText(Mathf.roundToInt(damage) + "", Color.createWhite(), go.getTransform().getPosition());
			}
		}

		flash.setEnabled(true);
		flash.setIntensity(2.0f);

		gun.setLocalPosition(gunPosition.sub(0, 0, recoil));

		trace.getGameObject().setActive(true);
		trace.setHit(hit);
		trace.getTransform().setPosition(barrelPosition);
		trace.getTransform().setRotation(cameraTransform.getRotation());

		traceFade = false;
	}

}
