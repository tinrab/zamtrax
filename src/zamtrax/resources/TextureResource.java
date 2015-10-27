package zamtrax.resources;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;

final class TextureResource extends ReferencedResource {

	private static Map<Integer, TextureResource> resourceMap = new HashMap<>();

	private final int id;
	private int textureId;

	private TextureResource(int id) {
		this.id = id;
		textureId = glGenTextures();
	}

	public int getTextureId() {
		return textureId;
	}

	@Override
	public void dispose() {
		glDeleteTextures(textureId);
		resourceMap.remove(id);
	}

	public static TextureResource create(Integer id) {
		TextureResource resource = resourceMap.get(id);

		if (resource == null) {
			resource = new TextureResource(id);

			resourceMap.put(id, resource);
		} else {
			resource.addReference();
		}

		return resource;
	}

}
