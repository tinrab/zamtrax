package zamtrax.ui;

import zamtrax.resources.Sprite;

public class Image extends Graphic {

	private Sprite sprite;

	@Override
	public void render(SpriteBatch spriteBatch) {
		spriteBatch.draw(sprite, 10, 10);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
