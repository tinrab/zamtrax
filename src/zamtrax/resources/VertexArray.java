package zamtrax.resources;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.*;

public class VertexArray {

	private BindingInfo bindingInfo;
	private FloatBuffer buffer;

	public VertexArray(int vertexCount, BindingInfo bindingInfo) {
		this.bindingInfo = bindingInfo;
		this.buffer = BufferUtils.createFloatBuffer(vertexCount * bindingInfo.getSize());
	}

	public void clear() {
		buffer.clear();
	}

	public VertexArray put(float f) {
		buffer.put(f);

		return this;
	}

	public void render(int mode, int first, int count) {
		int offset = 0;
		int stride = bindingInfo.getSize() * 4;

		for (int i = 0; i < bindingInfo.getAttributePointers().size(); i++) {
			AttributePointer ap = bindingInfo.getAttributePointers().get(i);

			buffer.position(offset);

			glEnableVertexAttribArray(ap.getLocation());
			glVertexAttribPointer(ap.getLocation(), ap.getAttributeType().getSize(), GL_FLOAT, false, stride, buffer);

			offset += ap.getAttributeType().getSize();
		}

		glDrawArrays(mode, first, count);

		bindingInfo.getAttributePointers().forEach(ap -> glDisableVertexAttribArray(ap.getLocation()));
	}

}
