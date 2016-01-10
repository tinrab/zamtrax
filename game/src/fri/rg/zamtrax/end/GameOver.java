package fri.rg.zamtrax.end;

import fri.rg.zamtrax.Util;
import fri.rg.zamtrax.level.Level;
import fri.rg.zamtrax.level.player.Player;
import fri.rg.zamtrax.menu.Button;
import fri.rg.zamtrax.menu.MainMenu;
import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.resources.Sprite;
import zamtrax.resources.Texture;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.Justify;
import zamtrax.ui.Canvas;
import zamtrax.ui.Text;

public class GameOver extends Scene {

	@Override
	public void onEnter() {
		super.onEnter();

		Input.setMouseLocked(false);

		Camera camera = GameObject.create().addComponent(Camera.class);
		Camera.setMainCamera(camera);
		camera.setProjection(Matrix4.createPerspective(30.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 100.0f));

		Canvas canvas = GameObject.create().addComponent(Canvas.class);
		canvas.setProjection(Matrix4.createOrthographic(0, Game.getScreenWidth(), Game.getScreenHeight(), 0, -10, 10));

		Texture uiTexture = Resources.loadTexture("textures/ui.png", Texture.Format.ARGB, Texture.WrapMode.CLAMP, Texture.FilterMode.NEAREST);
		BMFont boldFont = Resources.loadFont("fonts/boldFont.fnt");
		BMFont font = Resources.loadFont("fonts/font.fnt");

		Text gameOverText = GameObject.create().addComponent(Text.class);
		gameOverText.setText("GAME OVER");
		gameOverText.setFont(boldFont);
		gameOverText.getTransform().setScale(1.2f, 1.2f, 1);
		gameOverText.setHorizontalJustification(Justify.CENTER);
		gameOverText.getTransform().setPosition(Game.getScreenWidth() / 2.0f, 200.0f, 0.0f);

		Text scoreLabel = GameObject.create().addComponent(Text.class);
		scoreLabel.setText("Score");
		scoreLabel.setFont(font);
		scoreLabel.getTransform().setScale(0.6f, 0.6f, 1.0f);
		scoreLabel.setHorizontalJustification(Justify.CENTER);
		scoreLabel.getTransform().setPosition(Game.getScreenWidth() / 2.0f, 400.0f, 0.0f);

		float score = Player.getInstance().getScore();

		Text scoreText = GameObject.create().addComponent(Text.class);
		scoreText.setText(Util.formatTime(score));
		scoreText.setFont(font);
		scoreText.setHorizontalJustification(Justify.CENTER);
		scoreText.getTransform().setPosition(Game.getScreenWidth() / 2.0f, 450.0f, 0.0f);

		{
			Button backButton = GameObject.create(canvas.getGameObject()).addComponent(Button.class);
			backButton.setSprite(Sprite.fromTexture(uiTexture, 1, 1, 16, 16, 4, 4, 4, 4));
			backButton.setText("Back", font);

			backButton.getTransform().setPosition(Game.getScreenWidth() / 2.0f, Game.getScreenHeight() - 100.0f, 0.0f);

			backButton.setOnReleaseListener(() -> Game.getInstance().enterScene(MainMenu.class));
		}
	}

}
