package fri.rg.zamtrax.level;

import fri.rg.zamtrax.Engineer;
import fri.rg.zamtrax.Rotate;
import fri.rg.zamtrax.level.player.Player;
import zamtrax.*;
import zamtrax.components.*;
import zamtrax.resources.*;
import zamtrax.ui.Canvas;
import zamtrax.ui.Text;

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
		super.onEnter();

		instance = this;

		arena = new Arena();

		setAmbientLight(new Color(0.05f, 0.05f, 0.05f));

		{
			DirectionalLight dl1 = GameObject.create().addComponent(DirectionalLight.class);

			dl1.getTransform().setRotation(Quaternion.fromEuler(new Vector3(30, 30, 30).mul(Mathf.DEG_TO_RAD)));
			dl1.setColor(new Color(1.0f, 0.8f, 0.5f));
			dl1.setIntensity(0.5f);

			DirectionalLight dl2 = GameObject.create().addComponent(DirectionalLight.class);

			dl2.getTransform().setRotation(dl1.getTransform().getRotation().inverse());
			dl2.setColor(new Color(0.5f, 0.8f, 1.0f));
			dl2.setIntensity(0.5f);
		}

		GameObject.create().addComponent(Player.class);

		stdBindingInfo = new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.bind(AttributeType.NORMAL, 2, "normal")
				.build();


		Material stdMaterial = new Material(Resources.loadTexture("textures/grid.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));

		Engineer.createArena(arena, stdBindingInfo, stdMaterial);

		{
			Canvas canvas = GameObject.create().addComponent(Canvas.class);

			Text text = GameObject.create(canvas.getGameObject()).addComponent(Text.class);
			text.setText("Hello, World!");
			text.setFont(Resources.loadFont("fonts/font.fnt"));
			text.getTransform().setScale(new Vector3(0.5f, 0.5f, 0.5f));
		}
	}

	public Arena getArena() {
		return arena;
	}

}

