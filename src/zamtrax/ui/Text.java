package zamtrax.ui;

import zamtrax.Color;
import zamtrax.resources.bmfont.BMFont;

public class Text extends Graphic {

	private BMFont font;
	private Color color;
	private String text;

	@Override
	public void onAdd() {
		color = Color.createWhite();
	}

	@Override
	public void render(SpriteBatch spriteBatch) {
		//spriteBatch.setColor(color);
		//spriteBatch.draw(font, text, 100, 100, 0.4f, 0.4f);
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
