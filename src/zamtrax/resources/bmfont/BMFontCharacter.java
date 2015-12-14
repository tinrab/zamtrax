package zamtrax.resources.bmfont;

public final class BMFontCharacter {

	private char id;
	private int x, y, width, height;
	private int xoffset, yoffset, advance;
	private float u1, v1, u2, v2;

	public BMFontCharacter(char id, int x, int y, int width, int height, int xoffset, int yoffset, int advance, float u1, float v1, float u2, float v2) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.advance = advance;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
	}

	public char getId() {
		return id;
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

	public int getXoffset() {
		return xoffset;
	}

	public int getYoffset() {
		return yoffset;
	}

	public int getAdvance() {
		return advance;
	}

	public float getU1() {
		return u1;
	}

	public float getV1() {
		return v1;
	}

	public float getU2() {
		return u2;
	}

	public float getV2() {
		return v2;
	}

}
