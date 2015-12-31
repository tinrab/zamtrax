package zamtrax;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

final class Window implements Disposable {

	private int width, height;
	private boolean vSync;
	private String title;

	private long window;
	private Input input;

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWCursorPosCallback cursorPosCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback scrollCallback;

	Window(int width, int height, String title, boolean vSync) {
		this.width = width;
		this.height = height;
		this.title = title;
		this.vSync = vSync;
	}

	void show() {
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		if (glfwInit() != GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
		//glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		//glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

		window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

		if (window == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		input = new Input(window);

		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				input.invokeKey(key, scancode, action, mods);
			}

		});
		glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				input.invokeCursorPosition((float) xpos, (float) ypos);
			}

		});
		glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				input.invokeMouseButton(button, action, mods);
			}

		});
		glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback() {

			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				input.invokeScroll((float) xoffset, (float) yoffset);
			}

		});


		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
				window,
				(vidmode.getWidth() - width) / 2,
				(vidmode.getHeight() - height) / 2
		);

		glfwMakeContextCurrent(window);

		if (vSync) {
			glfwSwapInterval(1);
		}

		glfwShowWindow(window);

		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	boolean isClosed() {
		return glfwWindowShouldClose(window) != GL_FALSE;
	}

	void update() {
		glfwSwapBuffers(window);
		glfwPollEvents();
	}

	void updateInput() {
		input.update();
	}

	int getWidth() {
		return width;
	}

	int getHeight() {
		return height;
	}

	@Override
	public void dispose() {
		keyCallback.release();
		cursorPosCallback.release();
		mouseButtonCallback.release();
		scrollCallback.release();

		glfwDestroyWindow(window);
		glfwTerminate();
		errorCallback.release();
	}

}
