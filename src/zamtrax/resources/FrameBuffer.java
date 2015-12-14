package zamtrax.resources;

import org.lwjgl.opengl.GL;

public class FrameBuffer {

	public static boolean isSupported() {
		return GL.getCapabilities().GL_EXT_framebuffer_object;
	}

}
