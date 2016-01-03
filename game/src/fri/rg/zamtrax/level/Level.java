package fri.rg.zamtrax.level;

import fri.rg.zamtrax.Engineer;
import fri.rg.zamtrax.level.player.FreeLook;
import zamtrax.*;
import zamtrax.Transform;
import zamtrax.components.*;
import zamtrax.resources.*;

public class Level extends Scene {

	private static Level instance;

	public static Level getInstance() {
		return instance;
	}

	private Arena arena;

	private Shader stdShader;
	private Material stdMaterial;

	@Override
	public void onEnter() {
		super.onEnter();

		instance = this;

		arena = new Arena();

		setAmbientLight(new Color(0.1f, 0.1f, 0.1f));

		{
			DirectionalLight dl1 = GameObject.create().addComponent(DirectionalLight.class);

			dl1.getTransform().setRotation(Quaternion.fromEuler(-50, -45, -10));
			dl1.setColor(new Color(1.0f, 0.7f, 0.3f));
			dl1.setIntensity(1.0f);
			dl1.setShadows(Light.Shadows.HARD);

			/*
			SpotLight spotLight = GameObject.create().addComponent(SpotLight.class);

			spotLight.getTransform().setPosition(15, 10, 15);
			spotLight.getTransform().setRotation(Quaternion.fromEuler(50, 0, 0.0f));
			spotLight.setRange(70.0f);
			spotLight.setSpotAngle(80);

			spotLight.setColor(new Color(0.4f, 0.75f, 1));
			spotLight.getGameObject().addComponent(Rotate.class).setAngle(new Vector3(0, 60, 0));

			spotLight.setCookie(Resources.loadTexture("textures/bump.jpg", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.LINEAR));
			*/
		}

		//GameObject.create().addComponent(Player.class);
		Transform fl = GameObject.create().addComponent(FreeLook.class).getTransform();
		fl.setPosition(new Vector3(0, 5, 0));
		fl.setRotation(Quaternion.fromEuler(new Vector3(30, 0, 0).mul(Mathf.DEG_TO_RAD)));

		{
			Material stdMaterial = new Material("shaders/textured.vs", "shaders/textured.fs");

			stdMaterial.setTexture("diffuse", Resources.loadTexture("textures/grid.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));

			Engineer.createArena(arena, stdMaterial);
		}

		//Engineer.createBox(50, 1, 50, 1, stdBindingInfo, stdMaterial);
		//Engineer.createBox(1, 5, 1, 1, stdBindingInfo, stdMaterial).getTransform().setPosition(new Vector3(1, 0, 1));
/*
		GameObject sphere = GameObject.create();
		Material vertexColored = new Material("shaders/vertexColor.vs", "shaders/vertexColor.fs");

		sphere.addComponent(MeshFilter.class).setMesh(Resources.loadModel("models/sphere.ply"));
		sphere.addComponent(MeshRenderer.class).setMaterial(vertexColored);

		sphere.getTransform().setPosition(5, 10, 5);
*/
		//setFilter(1, "shaders/bloom.filter");
	}

	public Arena getArena() {
		return arena;
	}

}

