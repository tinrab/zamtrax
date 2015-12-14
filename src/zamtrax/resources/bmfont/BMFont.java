package zamtrax.resources.bmfont;

import zamtrax.resources.Texture;

import java.util.Map;

public final class BMFont {

	private Texture texture;
	private int lineHeight;
	private Map<Character, BMFontCharacter> chars;
	private Map<Integer, Map<Integer, Integer>> kernings;

	public BMFont(Texture texture, int lineHeight, Map<Character, BMFontCharacter> chars, Map<Integer, Map<Integer, Integer>> kernings) {
		this.texture = texture;
		this.lineHeight = lineHeight;
		this.chars = chars;
		this.kernings = kernings;
	}

	public BMFontCharacter getCharacter(char id) {
		return chars.get(id);
	}

	public int getKerning(char first, char second) {
		Map<Integer, Integer> f = kernings.get(first);

		if (f == null) {
			return 0;
		}

		Integer k = f.get(second);

		return k == null ? 0 : k;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public Texture getTexture() {
		return texture;
	}

	public float getStringWidth(String s, float scaleX) {
		char last = 0;
		float sizeX = 0;

		for (int i = 0; i < s.length(); i++) {
			char current = s.charAt(i);
			BMFontCharacter ch = chars.get(current);

			if (last != 0) {
				sizeX += getKerning(current, last) * scaleX;
			}

			sizeX += ch.getAdvance() * scaleX;

			last = current;
		}

		return sizeX;
	}

}
