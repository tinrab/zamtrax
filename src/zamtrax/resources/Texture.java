package zamtrax.resources;

import zamtrax.Disposable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public final class Texture implements Disposable {

	public enum WrapMode {
		CLAMP, REPEAT
	}

	public enum FilterMode {
		NEAREST, LINEAR, MIPMAP
	}

	public enum Format {
		ARGB
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

		int wm = -1, fm = -1;

		switch (wrapMode) {
			case CLAMP:
				wm = GL_CLAMP;
				break;
			case REPEAT:
				wm = GL_REPEAT;
				break;
		}

		switch (filterMode) {
			case LINEAR:
				fm = GL_LINEAR;
				break;
			case NEAREST:
				fm = GL_NEAREST;
				break;
		}

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wm);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wm);

		if (filterMode == FilterMode.MIPMAP) {
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_NEAREST);

			glGenerateMipmap(GL_TEXTURE_2D);
		} else {
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, fm);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, fm);
		}

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
