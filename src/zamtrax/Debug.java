package zamtrax;

import zamtrax.components.Camera;
import zamtrax.resources.*;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

public final class Debug {

	private VertexArray vertexArray;
	private Shader shader;

	private Debug() {
		BindingInfo bindingInfo = new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.COLOR, 1, "color")
				.build();
		vertexArray = new VertexArray(1000, bindingInfo);
		shader = new Shader(Resources.loadText("shaders/debug.vs", Debug.class.getClassLoader()), Resources.loadText("shaders/debug.fs", Debug.class.getClassLoader()), bindingInfo, Arrays.asList(new Uniform("MVP")));
	}

	public void drawLine(Vector3 origin, Vector3 direction, float distance, Color color) {
		drawLine(origin, origin.add(direction.mul(distance)), color);
	}

	public void drawLine(Vector3 start, Vector3 end, Color color) {
		vertexArray.clear();

		vertexArray.put(start.x, start.y, start.z, 1.0f);
		vertexArray.put(color.r, color.g, color.b, color.a);

		vertexArray.put(end.x, end.y, end.z, 1.0f);
		vertexArray.put(color.r, color.g, color.b, color.a);

		shader.bind();

		shader.setUniform("MVP", Camera.getMainCamera().getViewProjection());

		vertexArray.render(GL_LINES, 0, 2);

		shader.release();
	}

	public void drawGrid(Vector3 origin, int width, int height, float cellSize, Color color) {
		vertexArray.clear();

		for (int i = 0; i < width + 1; i++) {
			vertexArray.put(origin.x, origin.y, origin.z + i * cellSize, 1.0f);
			vertexArray.put(color.r, color.g, color.b, color.a);

			vertexArray.put(origin.x + width, origin.y, origin.z + i * cellSize, 1.0f);
			vertexArray.put(color.r, color.g, color.b, color.a);
		}

		for (int i = 0; i < height + 1; i++) {
			vertexArray.put(origin.x + i * cellSize, origin.y, origin.z, 1.0f);
			vertexArray.put(color.r, color.g, color.b, color.a);

			vertexArray.put(origin.x + i * cellSize, origin.y, origin.z + height, 1.0f);
			vertexArray.put(color.r, color.g, color.b, color.a);
		}

		shader.bind();

		shader.setUniform("MVP", Camera.getMainCamera().getViewProjection());

		vertexArray.render(GL_LINES, 0, (width + height) * 2 + 4);

		shader.release();
	}

	private static Debug instance;

	public static Debug getInstance() {
		if (instance == null) {
			instance = new Debug();
		}

		return instance;
	}

}