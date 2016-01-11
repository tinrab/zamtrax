package zamtrax.resources.bmfont;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import zamtrax.Resources;
import zamtrax.resources.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BMFontParser extends DefaultHandler {

	private Texture texture;
	private int lineHeight;
	private Map<Character, BMFontCharacter> chars;
	private Map<Integer, Map<Integer, Integer>> kernings;
	private String dir;

	public BMFontParser(String dir) {
		this.dir = dir;
		chars = new HashMap<>();
		kernings = new HashMap<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("page")) {
			parsePage(attributes);
		} else if (qName.equals("common")) {
			parseCommon(attributes);
		} else if (qName.equals("char")) {
			parseChar(attributes);
		} else if (qName.equals("kerning")) {
			parseKerning(attributes);
		}
	}

	private void parsePage(Attributes attributes) {
		String pathname = dir + "/" + attributes.getValue("file");

		texture = Resources.loadTexture(pathname, Texture.Format.ARGB, Texture.WrapMode.CLAMP, Texture.FilterMode.LINEAR);
	}

	private void parseCommon(Attributes attributes) {
		lineHeight = Integer.parseInt(attributes.getValue("lineHeight"));
	}

	private void parseChar(Attributes attributes) {
		char id = (char) Integer.parseInt(attributes.getValue("id"));
		int x = Integer.parseInt(attributes.getValue("x"));
		int y = Integer.parseInt(attributes.getValue("y"));
		int width = Integer.parseInt(attributes.getValue("width"));
		int height = Integer.parseInt(attributes.getValue("height"));
		int xoffset = Integer.parseInt(attributes.getValue("xoffset"));
		int yoffset = Integer.parseInt(attributes.getValue("yoffset"));
		int advance = Integer.parseInt(attributes.getValue("xadvance"));

		float w = 1.0f / texture.getWidth();
		float h = 1.0f / texture.getHeight();

		float u1 = x * w;
		float v1 = y * h;
		float u2 = (x + width) * w;
		float v2 = (y + height) * h;

		chars.put(id, new BMFontCharacter(id, x, y, width, height, xoffset, yoffset, advance, u1, v1, u2, v2));
	}

	private void parseKerning(Attributes attributes) {
		int first = Integer.parseInt(attributes.getValue("first"));
		int second = Integer.parseInt(attributes.getValue("second"));
		int amount = Integer.parseInt(attributes.getValue("amount"));

		if (!kernings.containsKey(first)) {
			kernings.put(first, new HashMap<>());
		}

		kernings.get(first).put(second, amount);
	}

	public BMFont getResult() {
		return new BMFont(texture, lineHeight, chars, kernings);
	}

}
