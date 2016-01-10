package fri.rg.zamtrax.level.player;

import fri.rg.zamtrax.end.GameOver;
import fri.rg.zamtrax.level.HurtFilter;
import fri.rg.zamtrax.level.Level;
import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.CharacterController;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
import zamtrax.resources.Material;
import zamtrax.resources.Texture;

public class Player extends Component {

	private static Player instance;

	public static Player getInstance() {
		return instance;
	}

	private float score;
	private float maxHealth;
	private float health;
	private HurtFilter hurtFilter;

	@Override
	public void onAdd() {
		instance = this;

		maxHealth = 1000.0f;
		health = maxHealth;

		Camera camera = GameObject.create(getGameObject()).addComponent(Camera.class);
		Camera.setMainCamera(camera);

		gameObject.setTag("player");

		camera.setProjection(Matrix4.createPerspective(60.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 600.0f));

		Material gunMaterial = new Material("shaders/gun.vs", "shaders/gun.fs");
		gunMaterial.setTexture("diffuse", Resources.loadTexture("textures/gun.png", Texture.Format.ARGB, Texture.WrapMode.CLAMP, Texture.FilterMode.LINEAR));
		GameObject gun = GameObject.create(camera.getGameObject());
		gun.addComponent(MeshFilter.class).setMesh(Resources.loadModel("models/gun.ply"));
		gun.addComponent(MeshRenderer.class).setMaterial(gunMaterial);

		gun.getTransform().setLocalPosition(0.07f, -0.04f, 0.4f);

		getTransform().setPosition(Level.getInstance().getFreeLocation().add(0, 10, 0));

		getGameObject().addComponent(CharacterController.class);
		getGameObject().addComponent(PlayerController.class);

		hurtFilter = new HurtFilter();
	}

	@Override
	public void update(float delta) {
		score += delta;

		checkDeath();

		health = Mathf.clamp(health + delta * (maxHealth / 20.0f), 0, maxHealth);

		hurtFilter.setIntensity((1.0f - health / maxHealth) / 2.0f);
	}

	private void checkDeath() {
		if (health <= 0.0f || Level.getInstance().getNodes().size() == 0 || Input.getKey(Input.KEY_ESCAPE) || transform.getPosition().y < -2) {
			Game.getInstance().enterScene(GameOver.class);
		}
	}

	public float getScore() {
		return score;
	}

	public void takeDamage(float damage) {
		health -= damage;
	}

	public HurtFilter getHurtFilter() {
		return hurtFilter;
	}
}
