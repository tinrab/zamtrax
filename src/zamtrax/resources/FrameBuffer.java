package zamtrax.resources;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import zamtrax.Disposable;
import zamtrax.Game;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

public class FrameBuffer implements Disposable {

	private static int frameBufferCount;

	public static boolean isSupported() {
		return GL.getCapabilities().GL_EXT_framebuffer_object;
	}

	private int id;
	private Texture texture;

	public FrameBuffer(int width, int height, Texture.WrapMode wrapMode, Texture.FilterMode filterMode) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		texture = new Texture(("FrameBuffer" + (frameBufferCount++)).hashCode(), width, height, buffer, Texture.Format.ARGB, wrapMode, filterMode);

		texture.bind();
		id = glGenFramebuffersEXT();

		glBindFramebufferEXT(GL_FRAMEBUFFER, id);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getResource().getTextureId(), 0);

		int result = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);

		if (result != GL_FRAMEBUFFER_COMPLETE) {
			glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
			glDeleteFramebuffers(id);

			throw new RuntimeException("exception " + result + " when checking FBO status");
		}

		glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
		texture.release();
	}

	public Texture getTexture() {
		return texture;
	}

	public void begin() {
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
	}

	public void end() {
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	@Override
	public void dispose() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		glDeleteFramebuffersEXT(id);
	}

}
