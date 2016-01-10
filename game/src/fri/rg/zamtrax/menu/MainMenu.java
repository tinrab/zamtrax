package fri.rg.zamtrax.menu;

import fri.rg.zamtrax.level.Level;
import zamtrax.*;
import zamtrax.components.*;
import zamtrax.rendering.Filter;
import zamtrax.resources.Material;
import zamtrax.resources.Sprite;
import zamtrax.resources.Texture;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.Justify;
import zamtrax.ui.Canvas;
import zamtrax.ui.Text;

public final class MainMenu extends Scene {

	@Override
	public void onEnter() {
		super.onEnter();

		{
			Camera camera = GameObject.create().addComponent(Camera.class);
			Camera.setMainCamera(camera);

			camera.setProjection(Matrix4.createPerspective(30.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 100.0f));
		}

		setAmbientLight(new Color(0.1f, 0.1f, 0.1f));

		{
			PointLight pl = GameObject.create().addComponent(PointLight.class);

			pl.setRange(10);
			pl.setColor(new Color(1.0f, 0.7f, 0.3f));
			pl.getTransform().setPosition(3, 0, 3);
			pl.getGameObject().addComponent(Oscillate.class);

			pl = GameObject.create().addComponent(PointLight.class);

			pl.setRange(10);
			pl.setColor(new Color(0.3f, 0.7f, 1.0f));
			pl.getTransform().setPosition(-3, 0, 3);
			pl.getGameObject().addComponent(Oscillate.class).setDelay(Mathf.PI);
		}

		{
			GameObject title = GameObject.create();
			Material material = new Material("shaders/title.vs", "shaders/title.fs");

			material.setSpecularIntensity(10.0f);
			material.setShininess(0.5f);

			title.addComponent(MeshFilter.class).setMesh(Resources.loadModel("models/title.ply"));
			title.addComponent(MeshRenderer.class).setMaterial(material);

			title.getTransform().setPosition(0.0f, 0.25f, 6.0f);
		}

		{
			GameObject particles = GameObject.create();
			ParticleSystem ps = particles.addComponent(ParticleSystem.class);
			particles.addComponent(ParticleEmitter.class);

			particles.getTransform().setPosition(0, 0, 8);
		}

		{
			Canvas canvas = GameObject.create().addComponent(Canvas.class);
			//canvas.setProjection(Matrix4.createTranslation(0.0f, 0.0f, 5.0f).mul(Matrix4.createOrthographic(0.0f, Game.getScreenWidth(), Game.getScreenHeight(), 0.0f, 0.0f, 10.0f)));
			canvas.setProjection(Matrix4.createOrthographic(0, Game.getScreenWidth(), Game.getScreenHeight(), 0, -10, 10));

			Texture uiTexture = Resources.loadTexture("textures/ui.png", Texture.Format.ARGB, Texture.WrapMode.CLAMP, Texture.FilterMode.NEAREST);

			Button startButton = GameObject.create(canvas.getGameObject()).addComponent(Button.class);
			startButton.setSprite(Sprite.fromTexture(uiTexture, 1, 1, 16, 16, 4, 4, 4, 4));
			BMFont font = Resources.loadFont("fonts/font.fnt");
			startButton.setText("Start", font);

			startButton.getTransform().setPosition(Game.getScreenWidth() / 2.0f, Game.getScreenHeight() - 350.0f, 0.0f);

			startButton.setOnReleaseListener(() -> Game.getInstance().enterScene(Level.class));

			Text copy = GameObject.create(canvas.getGameObject()).addComponent(Text.class);
			copy.setText("github.com/paidgeek/zamtrax");
			copy.setFont(font);
			copy.getTransform().setScale(0.24f, 0.24f, 1.0f);
			copy.setHorizontalJustification(Justify.CENTER);
			copy.getTransform().setPosition(Game.getScreenWidth() / 2.0f, Game.getScreenHeight() - 40.0f, 0.0f);
		}
	}

}
