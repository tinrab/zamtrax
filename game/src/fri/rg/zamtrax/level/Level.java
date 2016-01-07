package fri.rg.zamtrax.level;

import fri.rg.zamtrax.FlareFilter;
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
			dl1.setColor(new Color(1.0f, 0.8f, 0.5f));
			dl1.setIntensity(0.5f);
			dl1.setShadows(Light.Shadows.HARD);

			DirectionalLight dl2 = GameObject.create().addComponent(DirectionalLight.class);

			dl2.getTransform().setRotation(Quaternion.fromEuler(-50, -45 + 180, -10));
			dl2.setColor(new Color(0.5f, 0.8f, 1.0f));
			dl2.setIntensity(0.5f);
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
			ArenaFactory.generate(arena);
			ArenaFactory.createMesh(arena);
		}

		{/*
			GameObject sphere = GameObject.create();

			Material vertexColored = new Material("shaders/vertexColor.vs", "shaders/vertexColor.fs");

			sphere.addComponent(MeshFilter.class).setMesh(Resources.loadModel("models/sphere.ply"));
			sphere.addComponent(MeshRenderer.class).setMaterial(vertexColored);

			sphere.getTransform().setPosition(0, 10, 0);
			sphere.addComponent(SphereCollider.class);
			sphere.addComponent(RigidBody.class);*/
		}

		setFilter(1, "shaders/bloom.filter");
		FlareFilter flare = new FlareFilter();

		flare.setWorldPosition(new Vector3(Vector3.FORWARD).rotate(Quaternion.fromEuler(-50, -45, -10)).mul(1000.0f));

		addScreenFilter(flare);
	}

	public Arena getArena() {
		return arena;
	}

}

