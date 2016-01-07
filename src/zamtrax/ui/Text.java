package zamtrax.ui;

import zamtrax.Color;
import zamtrax.Transform;
import zamtrax.Vector3;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.Justify;

public class Text extends Graphic {

	private BMFont font;
	private Color color;
	private String text;
	private Justify horizontalJustification;
	private Justify verticalJustification;

	@Override
	public void onAdd() {
		color = Color.createWhite();
		text = "";
		horizontalJustification = Justify.LEFT;
		verticalJustification = Justify.TOP;
	}

	@Override
	public void render(SpriteBatch spriteBatch) {
		Vector3 position = transform.getPosition();
		Vector3 scale = transform.getScale();

		spriteBatch.setColor(color);
		spriteBatch.draw(font, text, position.x, position.y, scale.x, scale.y, horizontalJustification, verticalJustification);
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

	public Justify getHorizontalJustification() {
		return horizontalJustification;
	}

	public void setHorizontalJustification(Justify horizontalJustification) {
		this.horizontalJustification = horizontalJustification;
	}

	public Justify getVerticalJustification() {
		return verticalJustification;
	}

	public void setVerticalJustification(Justify verticalJustification) {
		this.verticalJustification = verticalJustification;
	}

}
