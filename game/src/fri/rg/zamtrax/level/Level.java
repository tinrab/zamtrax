package fri.rg.zamtrax.level;

import fri.rg.zamtrax.Engineer;
import fri.rg.zamtrax.Rotate;
import fri.rg.zamtrax.level.player.FreeLook;
import zamtrax.*;
import zamtrax.Transform;
import zamtrax.components.DirectionalLight;
import zamtrax.components.Light;
import zamtrax.components.SpotLight;
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
		super.onEnter();

		instance = this;

		arena = new Arena();

		setAmbientLight(new Color(0.2f, 0.2f, 0.2f));

		{
			DirectionalLight dl1 = GameObject.create().addComponent(DirectionalLight.class);

			dl1.getTransform().setRotation(Quaternion.fromEuler(new Vector3(-30, -30, -30)));
			dl1.getTransform().setPosition(new Vector3(0, 5, 0));
			dl1.setColor(new Color(1.0f, 0.8f, 0.5f));
			dl1.setIntensity(1.0f);
			dl1.setShadows(Light.Shadows.HARD);
			dl1.setShadowSoftness(0.5f);

			/*
			SpotLight spotLight = GameObject.create().addComponent(SpotLight.class);

			spotLight.getTransform().setPosition(new Vector3(3, 3, 3));
			spotLight.getTransform().setRotation(Quaternion.fromEuler(40, 0, 0.0f));
			spotLight.setRange(10.0f);
			spotLight.setSpotAngle(80);

			spotLight.setColor(new Color(0.4f, 0.75f, 1));
			spotLight.getGameObject().addComponent(Rotate.class).setAngle(new Vector3(0, 60, 0));

			spotLight.setShadows(Light.Shadows.HARD);
			spotLight.setLightBleed(1.0f);
			*/
		}

		/*
		{
			SpotLight sl = GameObject.create().addComponent(SpotLight.class);

			sl.setColor(new Color(0, 1, 0));
			sl.setRange(50);
			sl.setIntensity(10);

			sl.getTransform().setPosition(new Vector3(3, 5, 3));
			sl.getTransform().setRotation(Quaternion.fromEuler(new Vector3(30, 30, 30).mul(Mathf.DEG_TO_RAD)));

			sl.getGameObject().addComponent(Rotate.class).setAngle(new Vector3(3, 5, -3));
		}
		*/

		//GameObject.create().addComponent(Player.class);
		Transform fl = GameObject.create().addComponent(FreeLook.class).getTransform();
		fl.setPosition(new Vector3(0, 5, 0));
		fl.setRotation(Quaternion.fromEuler(new Vector3(30, 0, 0).mul(Mathf.DEG_TO_RAD)));

		stdBindingInfo = new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.bind(AttributeType.NORMAL, 2, "normal")
				.build();

		Material stdMaterial = new Material(Resources.loadTexture("textures/grid.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));

		{
			Engineer.createArena(arena, stdBindingInfo, stdMaterial);
			//Engineer.createBox(50, 1, 50, 1, stdBindingInfo, stdMaterial);
			//Engineer.createBox(1, 5, 1, 1, stdBindingInfo, stdMaterial).getTransform().setPosition(new Vector3(1, 0, 1));
		}
		/*
		{
			Canvas canvas = GameObject.create().addComponent(Canvas.class);

			Text text = GameObject.create(canvas.getGameObject()).addComponent(Text.class);
			text.setText("Hello, World!");
			text.setFont(Resources.loadFont("fonts/font.fnt"));
			text.getTransform().setScale(new Vector3(0.5f, 0.5f, 0.5f));
		}
		*/

		/*
		{
			GameObject box1 = Engineer.createBox(1, 1, 1, 1, stdBindingInfo, stdMaterial);
			GameObject box2 = Engineer.createBox(1, 1, 1, 1, stdBindingInfo, stdMaterial);

			box1.getTransform().setPosition(new Vector3(0, 1, 0));
			box1.getTransform().setRotation(Quaternion.fromEuler(45, 0, 0));
			box1.getTransform().setScale(new Vector3(2, 2, 2));

			box2.getTransform().setPosition(new Vector3(3, 0, 0));
			box2.getTransform().setRotation(Quaternion.fromEuler(0, 45, 0));
			box2.getTransform().setScale(new Vector3(0.5f, 0.5f, 0.5f));

			box1.setParent(box2, true);
		}
		*/
	}

	public Arena getArena() {
		return arena;
	}

}

