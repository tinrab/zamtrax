package fri.rg.zamtrax.level;

import fri.rg.zamtrax.Util;
import fri.rg.zamtrax.level.player.Player;
import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.resources.Sprite;
import zamtrax.resources.Texture;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.Justify;
import zamtrax.ui.Canvas;
import zamtrax.ui.Image;
import zamtrax.ui.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HUD extends Component {

	private static HUD instance;

	public static HUD getInstance() {
		return instance;
	}

	private class FloatingText {

		private Vector3 position;
		private Text text;
		private Vector3 velocity;
		private float age;

	}

	private BMFont font;

	private Level level;
	private Image[] indicators;
	private Text scoreText;
	private Text[] floatingTexts;
	private List<FloatingText> floatingTextList;

	@Override
	public void onAdd() {
		super.onAdd();

		instance = this;
		level = Level.getInstance();

		Canvas canvas = addComponent(Canvas.class);
		canvas.setProjection(Matrix4.createOrthographic(0, Game.getScreenWidth(), Game.getScreenHeight(), 0, -10, 10));

		Texture uiTexture = Resources.loadTexture("textures/hud.png", Texture.Format.ARGB, Texture.WrapMode.CLAMP, Texture.FilterMode.LINEAR);

		Image crosshair = GameObject.create(getGameObject()).addComponent(Image.class);
		crosshair.setSprite(Sprite.fromTexture(uiTexture, 5, 53, 48, 48));
		crosshair.getTransform().setPosition(Game.getScreenWidth() / 2.0f, Game.getScreenHeight() / 2.0f, 0.0f);

		indicators = new Image[3];

		for (int i = 0; i < 3; i++) {
			Image indicator = GameObject.create(getGameObject()).addComponent(Image.class);
			indicator.setSprite(Sprite.fromTexture(uiTexture, 2, 2, 34, 34));
			indicator.setEnabled(false);

			indicators[i] = indicator;
		}

		font = Resources.loadFont("fonts/font.fnt");

		floatingTextList = new ArrayList<>();

		scoreText = GameObject.create(gameObject).addComponent(Text.class);
		scoreText.setFont(font);
		scoreText.setHorizontalJustification(Justify.LEFT);
		scoreText.setVerticalJustification(Justify.TOP);
		scoreText.getTransform().setScale(0.6f, 0.6f, 1.0f);
		scoreText.getTransform().setPosition(6.0f, 0.0f, 0.0f);
	}

	@Override
	public void update(float delta) {
		Transform cam = Camera.getMainCamera().getTransform();
		int i = 0;

		for (Node node : level.getNodes()) {
			Vector3 viewportPoint = Camera.getMainCamera().worldToViewportPoint(node.getTransform().getPosition().add(0, 2, 0));
			Image indicator = indicators[i++];

			/*

				int s = (int) (angle / Mathf.PI2 * 4.0f);
				float perc = 1.0f - (angle / Mathf.PI2 * 4.0f) % 1.0f;
				float x = 0.0f;
				float y = 0.0f;

				switch (s) {
					case 0:
						x = Game.getScreenWidth();
						y = perc * Game.getScreenHeight();
						break;
					case 1:
						x = perc * Game.getScreenWidth();
						y = 0.0f;
						break;
					case 2:
						x = 0.0f;
						y = (1.0f - perc) * Game.getScreenHeight();
						break;
					case 3:
						x = (1.0f - perc) * Game.getScreenWidth();
						y = Game.getScreenHeight();
						break;
				}

				indicator.setPosition(x, y, 0.0f);
				*/

			if (viewportPoint.z > 0.0f) {
				indicator.setEnabled(true);

				viewportPoint.x = Mathf.clamp(viewportPoint.x, 0.0f, 1.0f);
				viewportPoint.y = Mathf.clamp(viewportPoint.y, 0.0f, 1.0f);

				Vector3 screenPoint = new Vector3(viewportPoint.x, 1.0f - viewportPoint.y, 0.0f).mul(Game.getScreenWidth(), Game.getScreenHeight(), 0.0f);

				indicator.getTransform().setPosition(screenPoint);
				indicator.setTint(Color.lerp(Color.createGreen(), Color.createRed(), 1.0f - node.getHealth() / node.getMaxHealth()));
			} else {
				indicator.setEnabled(false);
			}
		}

		if (level.getNodes().size() < indicators.length) {
			for (int j = indicators.length - 1; j >= level.getNodes().size(); j--) {
				indicators[j].setEnabled(false);
			}
		}

		scoreText.setText(Util.formatTime(Player.getInstance().getScore()));

		Iterator<FloatingText> floatingTextIterator = floatingTextList.iterator();

		while (floatingTextIterator.hasNext()) {
			FloatingText floatingText = floatingTextIterator.next();

			floatingText.age += delta;
			floatingText.position.set(floatingText.position.add(floatingText.velocity.mul(delta)));

			floatingText.velocity.set(floatingText.velocity.add(0, -3.0f * delta, 0.0f));

			if (floatingText.age >= 1.0f) {
				floatingText.text.getGameObject().destroy();
				floatingTextIterator.remove();
			} else {
				Vector3 viewportPoint = Camera.getMainCamera().worldToViewportPoint(floatingText.position);
				Vector3 screenPoint = new Vector3(viewportPoint.x, 1.0f - viewportPoint.y, 0.0f).mul(Game.getScreenWidth(), Game.getScreenHeight(), 0.0f);

				if (viewportPoint.z < 0.0f) {
					floatingText.text.setEnabled(false);
				} else {
					floatingText.text.setEnabled(true);
					floatingText.text.getTransform().setPosition(screenPoint);
				}
			}
		}
	}

	public void addFloatingText(String text, Color color, Vector3 worldPosition) {
		FloatingText floatingText = new FloatingText();

		floatingText.text = GameObject.create(gameObject).addComponent(Text.class);
		floatingText.position = worldPosition;
		floatingText.velocity = new Vector3(Random.randomFloat() - 0.5f, 2.5f, Random.randomFloat() - 0.5f);
		floatingText.text.getTransform().setScale(0.3f, 0.3f, 1.0f);
		floatingText.text.setFont(font);
		floatingText.text.setText(text);
		floatingText.text.setColor(color);
		floatingText.text.setHorizontalJustification(Justify.CENTER);
		floatingText.text.setVerticalJustification(Justify.CENTER);

		floatingTextList.add(floatingText);
	}

}
