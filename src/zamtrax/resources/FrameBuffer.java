package zamtrax.resources;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import zamtrax.Disposable;
import zamtrax.Mathf;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL12.*;

public class FrameBuffer implements Disposable {

	public static boolean isSupported() {
		return GL.getCapabilities().GL_EXT_framebuffer_object;
	}

	private int textureTarget;
	private int width, height;
	private IntBuffer textures;
	private int frameBufferId;
	private int renderBufferId;

	public FrameBuffer(int textureTarget, int width, int height, int filter, boolean clamp, int internalFormat, int format, int attachment) {
		this(textureTarget, width, height, new int[]{filter}, clamp, new int[]{internalFormat}, new int[]{format}, new int[]{attachment});
	}

	public FrameBuffer(int textureTarget, int width, int height, int[] filters, boolean clamp, int[] internalFormats, int[] formats, int[] attachments) {
		this.textureTarget = textureTarget;
		this.width = width;
		this.height = height;

		textures = BufferUtils.createIntBuffer(attachments.length);

		initTextures(filters, clamp, internalFormats, formats);
		initRenderTargets(attachments);
	}

	private void initTextures(int[] filters, boolean clamp, int[] internalFormats, int[] formats) {
		for (int i = 0; i < textures.capacity(); i++) {
			int id = glGenTextures();
			textures.put(id);

			glBindTexture(textureTarget, id);

			glTexParameteri(textureTarget, GL_TEXTURE_MIN_FILTER, filters[i]);
			glTexParameteri(textureTarget, GL_TEXTURE_MAG_FILTER, filters[i]);

			if (clamp) {
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			}

			glTexImage2D(textureTarget, 0, internalFormats[i], width, height, 0, formats[i], GL_UNSIGNED_BYTE, 0);


			if (filters[i] == GL_NEAREST_MIPMAP_NEAREST ||
					filters[i] == GL_NEAREST_MIPMAP_LINEAR ||
					filters[i] == GL_LINEAR_MIPMAP_NEAREST ||
					filters[i] == GL_LINEAR_MIPMAP_LINEAR) {
				glGenerateMipmap(textureTarget);

				float maxAnisotropy = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);

				glTexParameterf(textureTarget, GL_TEXTURE_MAX_ANISOTROPY_EXT, Mathf.clamp(0.0f, 8.0f, maxAnisotropy));
			} else {
				glTexParameteri(textureTarget, GL_TEXTURE_BASE_LEVEL, 0);
				glTexParameteri(textureTarget, GL_TEXTURE_MAX_LEVEL, 0);
			}
		}

		textures.flip();
	}

	private void initRenderTargets(int[] attachments) {
		if (attachments == null) {
			return;
		}

		IntBuffer drawBuffers = BufferUtils.createIntBuffer(32);

		boolean hasDepth = false;

		for (int i = 0; i < textures.capacity(); i++) {
			if (attachments[i] == GL_DEPTH_ATTACHMENT_EXT) {
				drawBuffers.put(GL_NONE);
				hasDepth = true;
			} else {
				drawBuffers.put(attachments[i]);
			}

			if (attachments[i] == GL_NONE) {
				continue;
			}

			if (frameBufferId == 0) {
				frameBufferId = glGenFramebuffersEXT();

				glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferId);
			}

			glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, attachments[i], textureTarget, textures.get(i), 0);
		}

		if (frameBufferId == 0) {
			return;
		}

		if (!hasDepth) {
			renderBufferId = glGenRenderbuffersEXT();

			glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, renderBufferId);
			glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT, width, height);
			glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, renderBufferId);
		}


		drawBuffers.flip();
		glDrawBuffers(drawBuffers);

		if (glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT) != GL_FRAMEBUFFER_COMPLETE_EXT) {
			throw new RuntimeException("failed to create frame buffer");
		}

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	public void bind(int texture, int sampler) {
		glActiveTexture(GL_TEXTURE0 + sampler);
		glBindTexture(textureTarget, textures.get(texture));
	}

	public void bindAsRenderTarget() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferId);
		glViewport(0, 0, width, height);
	}

	@Override
	public void dispose() {
		glDeleteTextures(textures);

		glDeleteFramebuffersEXT(frameBufferId);
		glDeleteRenderbuffers(renderBufferId);
	}

}
