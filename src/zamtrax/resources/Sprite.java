package zamtrax.resources;

public class Sprite {

	private Texture texture;
	private int x, y, width, height;
	private float u1, u2, v1, v2;

	public static Sprite fromTexture(Texture texture, int x, int y, int width, int height) {
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

}
