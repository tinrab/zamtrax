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

		shader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("shaders/ui.vs", getClass().getClassLoader()))
				.setFragmentShaderSource(Resources.loadText("shaders/ui.fs", getClass().getClassLoader()))
				.build();

		data = new VertexArray(size, bindingInfo);
		color = Color.createWhite();

		projection = Matrix4.createOrthographic(0.0f, Game.getScreenWidth(), Game.getScreenHeight(), 0.0f, -1.0f, 1.0f);
	}

	public Matrix4 getProjection() {
		return projection;
	}

	public void setProjection(Matrix4 projection) {
		this.projection = projection;
	}

	public void begin() {
		if (began) {
			throw new RuntimeException("begin() has already been called");
		}

		began = true;
		vertexCount = 0;
		texture = null;

		shader.bind();

		shader.setUniform("MVP", projection);
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

	public void draw(Texture texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2) {
		checkFlush(texture);

		putRect(x, y, width, height, u1, v1, u2, v2);
	}

	public void draw(Texture texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2, float left, float right, float top, float bottom, Vector3 s, boolean fillCenter) {
		checkFlush(texture);

		float du = u2 - u1;
		float dv = v2 - v1;

		float cu1 = u1 + left / width * du;
		float cv1 = v1 + top / height * dv;
		float cu2 = u2 - right / width * du;
		float cv2 = v2 - bottom / height * dv;

		// TODO optimize math

		// top left
		putRect(x, y, left, top, u1, v1, cu1, cv1);
		// bottom right
		putRect(x + width * s.x - right, y + height * s.y - bottom, right, bottom, cu2, cv2, u2, v2);
		// top right
		putRect(x + width * s.x - right, y, right, top, cu2, v1, u2, cv1);
		// bottom left
		putRect(x, y + height * s.y - bottom, left, bottom, u1, cv2, cu1, v2);

		// left
		putRect(x, y + top, left, height * s.y - top - bottom, u1, cv1, cu1, cv2);
		// right
		putRect(x + width * s.x - right, y + top, right, height * s.y - top - bottom, cu2, cv1, u2, cv2);
		// top
		putRect(x + left, y, width * s.x - left - right, top, cu1, v1, cu2, cv1);
		// bottom
		putRect(x + left, y + height * s.y - bottom, width * s.x - left - right, bottom, cu1, cv2, cu2, v2);

		// center
		if (fillCenter) {
			putRect(x + left, y + top, width * s.x - left - right, height * s.y - top - bottom, cu1, cv1, cu2, cv2);
		}
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

	private void putRect(float x, float y, float width, float height, float u1, float v1, float u2, float v2) {
		putVertex(x, y, u1, v1);
		putVertex(x, y + height, u1, v2);
		putVertex(x + width, y + height, u2, v2);

		putVertex(x, y, u1, v1);
		putVertex(x + width, y + height, u2, v2);
		putVertex(x + width, y, u2, v1);
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
		vertexCount = 0;
	}

	private void render() {
		if (texture != null) {
			shader.setUniform("diffuse", 0);
			texture.bind(0);
		}

		data.render(GL_TRIANGLES, 0, vertexCount);
	}

}
