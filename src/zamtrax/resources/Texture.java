package zamtrax.resources;

import zamtrax.Disposable;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public final class Texture implements Disposable {

	public enum WrapMode {
		CLAMP(GL_CLAMP), REPEAT(GL_REPEAT);

		private final int value;

		WrapMode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	public enum FilterMode {
		NEAREST(GL_NEAREST), LINEAR(GL_LINEAR);

		private final int value;

		FilterMode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	public enum Format {
		ARGB(BufferedImage.TYPE_INT_ARGB);

		private final int value;

		Format(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	private TextureResource resource;

	public Texture(int id, int width, int height, ByteBuffer buffer, Format format, WrapMode wrapMode, FilterMode filterMode) {
		resource = TextureResource.create(id);

		// TODO fix this mess

		int glFormat = -1;

		switch (format) {
			case ARGB:
				glFormat = GL_RGBA;
				break;
		}

		bind();

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode.getValue());
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode.getValue());

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMode.getValue());
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMode.getValue());

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, glFormat, GL_UNSIGNED_BYTE, buffer);

		release();
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, resource.getTextureId());
	}

	public void bind(int sampler) {
		glActiveTexture(GL_TEXTURE0 + sampler);
		glBindTexture(GL_TEXTURE_2D, resource.getTextureId());
	}

	public void release() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void dispose() {
		resource.removeReference();
	}

}
