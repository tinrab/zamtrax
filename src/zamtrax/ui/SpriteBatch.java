package zamtrax.ui;

import zamtrax.*;
import zamtrax.resources.*;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.BMFontCharacter;
import zamtrax.resources.bmfont.Justify;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class SpriteBatch {

	private boolean began;
	private BindingInfo bindingInfo;
	private Shader shader;
	private Matrix4 projection;
	private VertexArray data;
	private int vertexCount;
	private Color color;
	private Texture texture;

	public SpriteBatch() {
		this(1000);
	}

	public SpriteBatch(int size) {
		bindingInfo = new BindingInfo(AttributeType.POSITION, AttributeType.COLOR, AttributeType.UV);

		/*
		shader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("shaders/ui.vs", getClass().getClassLoader()))
				.setFragmentShaderSource(Resources.loadText("shaders/ui.fs", getClass().getClassLoader()))
				.setBindingInfo(bindingInfo)
				.addUniform("P")
				.build();
		*/

		data = new VertexArray(size, bindingInfo);
		color = Color.createWhite();

		resize(Game.getScreenWidth(), Game.getScreenHeight());
	}

	public void resize(int width, int height) {
		projection = Matrix4.createOrthographic(0.0f, width, height, 0.0f, -1.0f, 1.0f);
	}

	public void begin() {
		if (began) {
			throw new RuntimeException("begin() has already been called");
		}

		began = true;
		vertexCount = 0;
		texture = null;

		shader.bind();

		shader.setUniform("P", projection);
	}

	public void end() {
		if (!began) {
			throw new RuntimeException("Cannot call end() before begin");
		}

		began = false;

		flush();
		shader.release();
	}

	public void setColor(Color color) {
		this.color = new Color(color);
	}

	public void draw(Sprite sprite, float x, float y) {
		draw(sprite, x, y, sprite.getWidth(), sprite.getHeight());
	}

	public void draw(Sprite sprite, float x, float y, float width, float height) {
		draw(sprite.getTexture(), x, y, width, height, sprite.getU1(), sprite.getV1(), sprite.getU2(), sprite.getV2());
	}

	public void draw(Texture texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2) {
		checkFlush(texture);

		putVertex(x, y, u1, v1);
		putVertex(x, y + height, u1, v2);
		putVertex(x + width, y + height, u2, v2);

		putVertex(x, y, u1, v1);
		putVertex(x + width, y + height, u2, v2);
		putVertex(x + width, y, u2, v1);
	}

	public void draw(BMFont font, String text, float x, float y) {
		draw(font, text, x, y, 1.0f, 1.0f);
	}

	public void draw(BMFont font, String text, float x, float y, float sx, float sy) {
		draw(font, text, x, y, sx, sy, Justify.LEFT, Justify.TOP);
	}

	public void draw(BMFont font, String text, float x, float y, Justify horizontalJustification, Justify verticalJustification) {
		draw(font, text, x, y, 1.0f, 1.0f, horizontalJustification, verticalJustification);
	}

	public void draw(BMFont font, String text, float x, float y, float sx, float sy, Justify horizontalJustification, Justify verticalJustification) {
		float maxX = x;

		float yOffset = 0;

		if (verticalJustification == Justify.BOTTOM) {
			yOffset = font.getLineHeight() * sy;
		} else if (verticalJustification == Justify.TOP) {
			yOffset = 0;
		} else if (verticalJustification == Justify.CENTER) {
			yOffset = font.getLineHeight() / 2 * sy;
		} else {
			return;
		}

		if (horizontalJustification == Justify.RIGHT) {
			char last = 0;

			for (int i = text.length() - 1; i >= 0; i--) {
				char current = text.charAt(i);
				BMFontCharacter ch = font.getCharacter(current);

				draw(font, ch, maxX - ch.getWidth(), y - yOffset, sx, sy);

				maxX -= ch.getAdvance() * sx;

				if (last != 0) {
					maxX -= font.getKerning(current, last) * sx;
				}

				last = current;
			}
		} else if (horizontalJustification == Justify.LEFT) {
			char last = 0;

			for (int i = 0; i < text.length(); i++) {
				char current = text.charAt(i);
				BMFontCharacter ch = font.getCharacter(current);

				if (last != 0) {
					maxX += font.getKerning(current, last) * sx;
				}

				draw(font, ch, maxX, y - yOffset, sx, sy);

				maxX += ch.getAdvance() * sx;

				last = current;
			}
		} else if (horizontalJustification == Justify.CENTER) {
			char last = 0;
			maxX -= font.getStringWidth(text, sx) / 2;

			for (int i = 0; i < text.length(); i++) {
				char current = text.charAt(i);
				BMFontCharacter ch = font.getCharacter(current);

				if (last != 0) {
					maxX += font.getKerning(current, last) * sx;
				}

				draw(font, ch, maxX, y - yOffset, sx, sy);

				maxX += ch.getAdvance() * sx;

				last = current;
			}
		}
	}

	private void draw(BMFont font, BMFontCharacter ch, float x, float y, float sx, float sy) {
		draw(font.getTexture(), x + ch.getXoffset() * sx, y + ch.getYoffset() * sy, ch.getWidth() * sx, ch.getHeight() * sy, ch.getU1(), ch.getV1(), ch.getU2(), ch.getV2());
	}

	public void putVertex(float x, float y, float u, float v) {
		data.put(x).put(y).put(0.0f).put(1.0f);
		data.put(color.r).put(color.g).put(color.b).put(color.a);
		data.put(u).put(v);

		vertexCount++;
	}

	private void checkFlush(Texture texture) {
		if (texture == null) {
			throw new RuntimeException("texture is null");
		}

		if (!texture.equals(this.texture)) {
			flush();

			this.texture = texture;
		}
	}

	private void flush() {
		render();
		data.clear();
	}

	private void render() {
		if (texture != null) {
			texture.bind();
		}

		data.render(GL_TRIANGLES, 0, vertexCount);
	}

}
