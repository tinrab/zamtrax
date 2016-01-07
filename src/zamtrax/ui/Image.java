package zamtrax.ui;

import zamtrax.*;
import zamtrax.resources.Sprite;

public class Image extends Graphic {

	public enum Type {
		SIMPLE, SLICED, TILED, FILED
	}

	private Sprite sprite;
	private Color tint;
	private Type type;
	private boolean fillCenter;

	@Override
	public void onAdd() {
		super.onAdd();

		tint = Color.createWhite();
		type = Type.SIMPLE;
		fillCenter = true;
	}

	@Override
	public void render(SpriteBatch spriteBatch) {
		Vector3 s = transform.getScale();
		Vector3 pos = transform.getPosition().sub(sprite.getPivot().x * s.x, sprite.getPivot().y * s.y, 0.0f);

		spriteBatch.setColor(tint);

		switch (type) {
			case SIMPLE:
				spriteBatch.draw(sprite.getTexture(),
						pos.x, pos.y, sprite.getWidth() * s.x, sprite.getHeight() * s.y,
						sprite.getU1(), sprite.getV1(), sprite.getU2(), sprite.getV2());
				break;
			case SLICED:
				spriteBatch.draw(sprite.getTexture(), pos.x, pos.y, sprite.getWidth(), sprite.getHeight(),
						sprite.getU1(), sprite.getV1(), sprite.getU2(), sprite.getV2(),
						sprite.getLeft(), sprite.getRight(), sprite.getTop(), sprite.getBottom(), s, fillCenter);
				break;
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;

		bounds = new Rectangle(-sprite.getPivot().x, -sprite.getPivot().y, sprite.getWidth(), sprite.getHeight());
	}

	public Color getTint() {
		return tint;
	}

	public void setTint(Color tint) {
		this.tint = tint;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean filledCenter() {
		return fillCenter;
	}

	public void setFillCenter(boolean fillCenter) {
		this.fillCenter = fillCenter;
	}

}
