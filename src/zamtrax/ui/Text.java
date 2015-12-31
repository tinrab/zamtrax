package zamtrax.ui;

import zamtrax.Color;
import zamtrax.Transform;
import zamtrax.resources.bmfont.BMFont;

public class Text extends Graphic {

	private Transform transform;
	private BMFont font;
	private Color color;
	private String text;

	@Override
	public void onAdd() {
		transform = getTransform();
		color = Color.createWhite();
	}

	@Override
	public void render(SpriteBatch spriteBatch) {
		float x = transform.getPosition().x;
		float y = transform.getPosition().y;
		float sx = transform.getScale().x;
		float sy = transform.getScale().y;

		spriteBatch.setColor(color);
		spriteBatch.draw(font, text, x, y, sx, sy);
	}

	public BMFont getFont() {
		return font;
	}

	public void setFont(BMFont font) {
		this.font = font;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
