package fri.rg.zamtrax.level;

import zamtrax.*;
import zamtrax.components.BoxCollider;
import zamtrax.components.Light;
import zamtrax.components.RigidBody;
import zamtrax.components.SpotLight;
import zamtrax.resources.Texture;

public class Node extends Component {

	private Transform top;

	private float maxHealth;
	private float health;
	private Color spotColor;
	private SpotLight spotLight;

	@Override
	public void onAdd() {
		super.onAdd();

		maxHealth = 5000.0f;
		health = maxHealth;

		gameObject.setTag("node");

		top = gameObject.getChildByIndex(0).getTransform();

		Texture cookie = Resources.loadTexture("textures/cookie.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST);

		spotLight = GameObject.create(top.getGameObject()).addComponent(SpotLight.class);
		spotColor = new Color(0.2f, 1.0f, 0.4f);
		spotLight.setSpotAngle(70.0f);
		spotLight.setCookie(cookie);
		spotLight.setShadows(Light.Shadows.HARD);
		spotLight.setColor(spotColor);
		spotLight.getTransform().setLocalRotation(Quaternion.fromEuler(0.0f, 180.0f, 0.0f));
		spotLight.getTransform().setLocalPosition(0, 1, 0);
		spotLight.setIntensity(1.5f);

		gameObject.addComponent(BoxCollider.class).setSize(0.5f, 2.0f, 0.5f);
		gameObject.addComponent(RigidBody.class).setKinematic(true);

		top.rotate(0, Random.randomFloat() * Mathf.PI2, 0);
	}

	@Override
	public void update(float delta) {
		top.rotate(0, delta * 60.0f, 0.0f, Space.SELF);

		spotLight.setColor(Color.lerp(spotLight.getColor(), spotColor, delta));

		if (health <= 0.0f) {
			gameObject.destroy();

			Level.getInstance().getNodes().remove(this);
		}
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public void takeDamage(float damage) {
		health -= damage;

		spotLight.setColor(new Color(1.0f, 0.2f, 0.1f));
	}

}
