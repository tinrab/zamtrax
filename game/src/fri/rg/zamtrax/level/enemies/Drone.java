package fri.rg.zamtrax.level.enemies;

import fri.rg.zamtrax.level.*;
import fri.rg.zamtrax.level.pathfinding.Agent;
import fri.rg.zamtrax.level.pathfinding.Path;
import fri.rg.zamtrax.level.player.Player;
import zamtrax.*;
import zamtrax.components.PointLight;
import zamtrax.components.RigidBody;

public class Drone extends Component implements Agent {

	private static final float SPEED = 4.0f;

	private enum State {
		IDLE, FOLLOW_PATH, DEAD, ATTACING
	}

	private Transform top;
	private Transform bottom;
	private Quaternion targetRotation;
	private State state;
	private Node targetNode;

	private Path path;
	private int step;

	private float maxHealth;
	private float health;

	private Transform target;
	private PointLight flash;
	private Trace trace;
	private boolean traceFade;
	private float cooldown;

	@Override
	public void onAdd() {
		super.onAdd();

		top = gameObject.getChildByIndex(0).getTransform();
		bottom = gameObject.getChildByIndex(1).getTransform();

		maxHealth = 1000.0f;
		health = maxHealth;

		flash = GameObject.create(gameObject).addComponent(PointLight.class);
		flash.getTransform().setLocalPosition(0.1f, -0.17f, 0.25f);
		flash.setColor(new Color(1.0f, 0.9f, 0.7f));
		flash.setIntensity(0.0f);
		flash.setEnabled(false);

		trace = GameObject.create().addComponent(Trace.class);
		trace.getGameObject().setActive(false);

		targetRotation = Quaternion.createIdentity();

		findPath();
		state = State.FOLLOW_PATH;
	}

	@Override
	public void update(float delta) {
		if (health <= 0.0f) {
			state = State.DEAD;

			gameObject.destroy();
			trace.getGameObject().destroy();

			return;
		}

		top.setRotation(Quaternion.slerp(top.getRotation(), targetRotation, delta * 10.0f));

		flash.setIntensity(flash.getIntensity() - delta * 40.0f);

		if (traceFade) {
			trace.getGameObject().setActive(false);
		} else {
			traceFade = true;
		}

		if (flash.getIntensity() < 0.0f) {
			flash.setEnabled(false);
			flash.setIntensity(0.0f);
		}

		if (state != State.DEAD) {
			bottom.rotate(0, 200.0f * delta, 0);

			switch (state) {
				case FOLLOW_PATH:
					followPath(delta);
					break;
				case ATTACING:
					attacking(delta);
					break;
			}
		}
	}

	private void attacking(float delta) {
		targetRotation = Quaternion.fromLookAt(target.getPosition().sub(top.getTransform().getPosition()), Vector3.UP);

		if (!isTargetVisible()) {
			findPath();
			state = State.FOLLOW_PATH;

			return;
		}

		Vector3 d = target.getPosition().sub(transform.getPosition());
		d.y = 0.0f;
		transform.translate(d.normalized().mul(delta * SPEED * (d.length() / 30.0f)));

		cooldown -= delta;

		if (cooldown <= 0.0f) {
			cooldown = 0.25f;

			fire();
		}
	}

	private void fire() {
		RaycastHit hit = Physics.getInstance().raycast(transform.getPosition(), top.forward(), 50.0f);

		if (hit != null) {
			GameObject go = ((Component) hit.getHitObject()).getGameObject();
			float damage = Random.gauss() * 10.0f + 100.0f;

			if (go.compareTag("node")) {
				go.getComponent(Node.class).takeDamage(damage);
				HUD.getInstance().addFloatingText(Mathf.roundToInt(damage) + "", Color.createRed(), go.getTransform().getPosition());
			} else if (go.compareTag("player")) {
				go.getComponent(Player.class).takeDamage(damage);
			}
		}

		flash.setEnabled(true);
		flash.setIntensity(2.0f);

		trace.getGameObject().setActive(true);
		trace.setHit(hit);
		trace.getTransform().setPosition(top.getPosition().add(0, 0.2f, 0));
		trace.getTransform().setRotation(top.getRotation());

		traceFade = false;
	}

	private void followPath(float delta) {
		Vector3 waypoint = new Vector3(path.getX(step) + 0.5f, 0.0f, path.getY(step) + 0.5f).mul(Chunk.BLOCK_SIZE);

		Vector3 off = transform.getPosition().sub(waypoint.x, 0.0f, waypoint.z);
		off.y = 0.0f;
		float dst = off.length();

		//targetRotation = Quaternion.fromLookAt(off.mul(-1.0f), Vector3.UP);
		transform.translate(off.normalized().mul(-delta * SPEED));

		if (dst < 0.5f) {
			step++;
		}

		if (isNodeVisible()) {
			target = targetNode.getTransform();
			state = State.ATTACING;
		}

		if (isPlayerVisible()) {
			target = Player.getInstance().getTransform();
			state = State.ATTACING;
		}
	}

	private void findPath() {
		Vector3 pos = transform.getPosition();
		int closest = Integer.MAX_VALUE;

		for (Node node : Level.getInstance().getNodes()) {
			Vector3 np = node.getTransform().getPosition();

			int sx = (int) (pos.x / Chunk.BLOCK_SIZE);
			int sy = (int) (pos.z / Chunk.BLOCK_SIZE);
			int tx = (int) (np.x / Chunk.BLOCK_SIZE);
			int ty = (int) (np.z / Chunk.BLOCK_SIZE);

			Path path = Level.getInstance().getArena().findPath(this, sx, sy, tx, ty);

			if (path == null) {
				// err
				gameObject.destroy();
				trace.getGameObject().destroy();

				return;
			}

			if (path.getLength() < closest) {
				closest = path.getLength();

				this.path = path;
				targetNode = node;
			}
		}

		step = 0;
	}

	private boolean isPlayerVisible() {
		RaycastHit hit = Physics.getInstance().raycast(top.getPosition(), Player.getInstance().getTransform().getPosition());

		return hit != null && ((Component) hit.getHitObject()).getGameObject().compareTag("player") && hit.getDistance() < 15.0f;
	}

	private boolean isNodeVisible() {
		RaycastHit hit = Physics.getInstance().raycast(top.getPosition(), targetNode.getTransform().getPosition());

		return hit != null && ((Component) hit.getHitObject()).getGameObject().compareTag("node") && hit.getDistance() < 15.0f;
	}

	private boolean isTargetVisible() {
		RaycastHit hit = Physics.getInstance().raycast(top.getPosition(), target.getPosition());

		return hit != null && ((Component) hit.getHitObject()).getTransform() == target && hit.getDistance() < 15.0f;
	}

	public void takeDamage(float damage) {
		health -= damage;
	}

}
