package zamtrax.resources;

import zamtrax.Vector2;

public class Sprite {

	private Texture texture;
	private int x, y, width, height;
	private float u1, u2, v1, v2;
	private float left, right, top, bottom;
	private Vector2 pivot;

	public static Sprite fromTexture(Texture texture, int x, int y, int width, int height) {
		return fromTexture(texture, x, y, width, height, 0, 0, 0, 0);
	}

	public static Sprite fromTexture(Texture texture, int x, int y, int width, int height, float left, float right, float top, float bottom) {
		Sprite sprite = new Sprite();

		sprite.texture = texture;

		sprite.x = x;
		sprite.y = y;
		sprite.width = width;
		sprite.height = height;

		float w = 1.0f / texture.getWidth();
		float h = 1.0f / texture.getHeight();

		sprite.u1 = x * w;
		sprite.v1 = y * h;
		sprite.u2 = (x + width) * w;
		sprite.v2 = (y + height) * h;

		sprite.left = left;
		sprite.right = right;
		sprite.top = top;
		sprite.bottom = bottom;

		sprite.pivot = new Vector2(width / 2.0f, height / 2.0f);

		return sprite;
	}

	public Texture getTexture() {
		return texture;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getU1() {
		return u1;
	}

	public float getU2() {
		return u2;
	}

	public float getV1() {
		return v1;
	}

	public float getV2() {
		return v2;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		return right;
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		return bottom;
	}

	public Vector2 getPivot() {
		return pivot;
	}

	public void setPivot(Vector2 pivot) {
		this.pivot = pivot;
	}

}
