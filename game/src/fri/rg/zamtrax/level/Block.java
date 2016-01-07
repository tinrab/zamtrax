package fri.rg.zamtrax.level;

import zamtrax.Color;

public enum Block {

	NULL(null),
	BASIC(new Color(0.2f, 0.2f, 0.2f)),
	ORE(new Color(0.2f, 0.5f, 1.0f));

	private final Color color;

	Block(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
