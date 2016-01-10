package fri.rg.zamtrax.level;

import zamtrax.Color;

public enum Block {

	NULL(null),
	BASIC(new Color(0.2f, 0.2f, 0.2f)),
	BRIGHT(new Color(0.8f, 0.8f, 0.8f));

	private final Color color;

	Block(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
