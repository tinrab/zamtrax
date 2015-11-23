package fri.rg.zamtrax.level;

import fri.rg.zamtrax.Engineer;
import fri.rg.zamtrax.level.player.Player;
import zamtrax.*;
import zamtrax.lights.SpotLight;
import zamtrax.resources.*;

public class Level extends Scene {

	private static Level instance;

	public static Level getInstance() {
		return instance;
	}

	private Arena arena;

	private BindingInfo stdBindingInfo;
	private Shader stdShader;
	private Material stdMaterial;

	@Override
	public void onEnter() {
		instance = this;

		arena = new Arena();

		setAmbientLight(new Color(0.1f, 0.1f, 0.1f));

		GameObject.create().addComponent(Player.class);

		stdBindingInfo = new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.bind(AttributeType.NORMAL, 2, "normal")
				.build();

		stdShader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("shaders/standard.vs"))
				.setFragmentShaderSource(Resources.loadText("shaders/standard.fs"))
				.setBindingInfo(stdBindingInfo)
				.addTransformationUniforms()
				.addLightsUniforms()
				.build();

		stdMaterial = new Material.Builder()
				.setShader(stdShader)
				.setTexture(Resources.loadTexture("textures/grid.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST))
				.build();

		{

			GameObject floor = Engineer.createBox(Arena.SIZE, 1, Arena.SIZE, 1.0f, stdBindingInfo, stdMaterial);
			GameObject roof = Engineer.createBox(Arena.SIZE, 1, Arena.SIZE, 1.0f, stdBindingInfo, stdMaterial);

			GameObject south = Engineer.createBox(Arena.SIZE, Arena.HEIGHT, 1, 1.0f, stdBindingInfo, stdMaterial);
			GameObject north = Engineer.createBox(Arena.SIZE, Arena.HEIGHT, 1, 1.0f, stdBindingInfo, stdMaterial);

			GameObject east = Engineer.createBox(1, Arena.HEIGHT, Arena.SIZE, 1.0f, stdBindingInfo, stdMaterial);
			GameObject west = Engineer.createBox(1, Arena.HEIGHT, Arena.SIZE, 1.0f, stdBindingInfo, stdMaterial);

			floor.getTransform().translate(0.0f, -0.5f, 0.0f);

			roof.getTransform().translate(0.0f, Arena.HEIGHT + 0.5f, 0.0f);

			south.getTransform().translate(0.0f, Arena.HEIGHT / 2.0f, -Arena.SIZE / 2.0f - 0.5f);
			north.getTransform().translate(0.0f, Arena.HEIGHT / 2.0f, Arena.SIZE / 2.0f + 0.5f);

			east.getTransform().translate(-Arena.SIZE / 2.0f - 0.5f, Arena.HEIGHT / 2.0f, 0.0f);
			west.getTransform().translate(Arena.SIZE / 2.0f + 0.5f, Arena.HEIGHT / 2.0f, 0.0f);


			setupKinematicBox(floor);
			setupKinematicBox(roof);
			setupKinematicBox(south);
			setupKinematicBox(north);
			setupKinematicBox(east);
			setupKinematicBox(west);
		}
	}

	private void setupKinematicBox(GameObject gameObject) {
		gameObject.addComponent(RigidBody.class).setKinematic(true);
	}

	public Arena getArena() {
		return arena;
	}

}

