package zamtrax.resources;

import org.lwjgl.opengl.GL;
import zamtrax.Disposable;
import zamtrax.Game;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.*;

public class FrameBuffer implements Disposable {

	public static boolean isSupported() {
		return GL.getCapabilities().GL_EXT_framebuffer_object;
	}

	private int width, height;
	private int colorTextureId;
	private int depthTextureId;
	private int frameBufferId;

	public FrameBuffer(int width, int height) {
		this.width = width;
		this.height = height;

		colorTextureId = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, colorTextureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGRA, GL_UNSIGNED_INT, (ByteBuffer) null);

		depthTextureId = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, depthTextureId);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);

		frameBufferId = glGenFramebuffersEXT();
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferId);

		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorTextureId, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, depthTextureId, 0);

		int status = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);

		if (status != GL_FRAMEBUFFER_COMPLETE_EXT) {
			dispose();

			throw new RuntimeException("failed to create frame buffer");
		}
	}

	public void bind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferId);
		glViewport(0, 0, width, height);
	}

	public void release() {
		glViewport(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	public int getColorTextureId() {
		return colorTextureId;
	}

	public int getDepthTextureId() {
		return depthTextureId;
	}

	@Override
	public void dispose() {
		glDeleteTextures(colorTextureId);
		glDeleteTextures(depthTextureId);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		glDeleteFramebuffersEXT(frameBufferId);
	}

}
