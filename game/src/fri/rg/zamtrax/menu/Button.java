package fri.rg.zamtrax.menu;

import zamtrax.*;
import zamtrax.resources.Sprite;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.Justify;
import zamtrax.ui.Image;
import zamtrax.ui.Text;

public class Button extends Component {

	private Image image;
	private Text text;
	private Color normalColor, pressedColor;

	@Override
	public void onAdd() {
		super.onAdd();

		normalColor = Color.createWhite();
		pressedColor = new Color(0.5f, 0.5f, 0.5f);
	}

	@Override
	public void update(float delta) {
		if (image.isPressed()) {
			image.setTint(pressedColor);
		} else {
			image.setTint(normalColor);
		}
	}

	public void setSprite(Sprite sprite) {
		image = GameObject.create(gameObject).addComponent(Image.class);

		image.setSprite(sprite);
		image.setType(Image.Type.SLICED);
		image.getTransform().setScale(new Vector3(16, 4, 1));
	}

	public void setText(String string, BMFont font) {
		text = GameObject.create(gameObject).addComponent(Text.class);

		text.setText(string);
		text.setFont(font);
		text.setColor(Color.createWhite());
		text.setHorizontalJustification(Justify.CENTER);
		text.setVerticalJustification(Justify.CENTER);

		text.getTransform().setPosition(0.0f, 0.0f, 0.0f);
		text.getTransform().setScale(0.7f, 0.7f, 1.0f);
	}

}
