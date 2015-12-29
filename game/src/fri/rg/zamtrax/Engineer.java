package fri.rg.zamtrax;

import fri.rg.zamtrax.level.Arena;
import zamtrax.*;
import zamtrax.components.BoxCollider;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
import zamtrax.components.RigidBody;
import zamtrax.resources.BindingInfo;
import zamtrax.resources.Material;
import zamtrax.resources.Mesh;

public final class Engineer {

	public static GameObject createBox(float width, float height, float depth, float uvScale, BindingInfo bindingInfo, Material material) {
		Vertex[] vertices = {
				new Vertex(-0.5f, 0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(-0.5f, -0.5f, -0.5f, 1.0f, 0.0f),
				new Vertex(-0.5f, -0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(0.5f, 0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(0.5f, -0.5f, -0.5f, 1.0f, 0.0f),
				new Vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.0f),
				new Vertex(0.5f, 0.5f, 0.5f, 1.0f, 1.0f),
				new Vertex(0.5f, -0.5f, 0.5f, 1.0f, 0.0f),
				new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.0f),
				new Vertex(-0.5f, 0.5f, 0.5f, 1.0f, 1.0f),
				new Vertex(-0.5f, -0.5f, 0.5f, 1.0f, 0.0f),
				new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(0.5f, -0.5f, -0.5f, 1.0f, 0.0f),
				new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(0.5f, 0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(-0.5f, 0.5f, -0.5f, 1.0f, 0.0f),
				new Vertex(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(-0.5f, 0.5f, 0.5f, 0.0f, 1.0f),
				new Vertex(-0.5f, 0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(-0.5f, -0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(-0.5f, 0.5f, -0.5f, 0.0f, 1.0f),
				new Vertex(0.5f, 0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(-0.5f, -0.5f, -0.5f, 0.0f, 0.0f),
				new Vertex(0.5f, 0.5f, -0.5f, 0.0f, 1.0f),
				new Vertex(0.5f, 0.5f, 0.5f, 1.0f, 1.0f),
				new Vertex(0.5f, -0.5f, -0.5f, 0.0f, 0.0f),
				new Vertex(0.5f, 0.5f, 0.5f, 0.0f, 1.0f),
				new Vertex(-0.5f, 0.5f, 0.5f, 1.0f, 1.0f),
				new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(-0.5f, -0.5f, 0.5f, 0.0f, 1.0f),
				new Vertex(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(0.5f, -0.5f, 0.5f, 0.0f, 0.0f),
				new Vertex(0.5f, 0.5f, 0.5f, 0.0f, 1.0f),
				new Vertex(0.5f, 0.5f, -0.5f, 1.0f, 1.0f),
				new Vertex(-0.5f, 0.5f, 0.5f, 0.0f, 0.0f)
		};

		int[] indices = {
				0, 1, 2,
				3, 4, 5,
				6, 7, 8,
				9, 10, 11,
				12, 13, 14,
				15, 16, 17,
				18, 19, 20,
				21, 22, 23,
				24, 25, 26,
				27, 28, 29,
				30, 31, 32,
				33, 34, 35,
		};

		for (int i = 0; i < indices.length; i += 3) {
			Vertex v1 = vertices[indices[i + 0]];
			Vertex v2 = vertices[indices[i + 1]];
			Vertex v3 = vertices[indices[i + 2]];

			v1.position = v1.position.mul(width, height, depth);
			v2.position = v2.position.mul(width, height, depth);
			v3.position = v3.position.mul(width, height, depth);

			if (v1.position.x == v2.position.x && v2.position.x == v3.position.x) {
				v1.uv = v1.uv.mul(depth * uvScale, height * uvScale);
				v2.uv = v2.uv.mul(depth * uvScale, height * uvScale);
				v3.uv = v3.uv.mul(depth * uvScale, height * uvScale);
			} else if (v1.position.z == v2.position.z && v2.position.z == v3.position.z) {
				v1.uv = v1.uv.mul(width * uvScale, height * uvScale);
				v2.uv = v2.uv.mul(width * uvScale, height * uvScale);
				v3.uv = v3.uv.mul(width * uvScale, height * uvScale);
			} else if (v1.position.y == v2.position.y && v2.position.y == v3.position.y) {
				v1.uv = v1.uv.mul(width * uvScale, depth * uvScale);
				v2.uv = v2.uv.mul(width * uvScale, depth * uvScale);
				v3.uv = v3.uv.mul(width * uvScale, depth * uvScale);
			}
		}

		Mesh mesh = new Mesh.Builder()
				.setVertices(vertices)
				.setIndices(indices)
				.calculateNormals()
				.setBindingInfo(bindingInfo)
				.build();

		GameObject box = GameObject.create();

		box.addComponent(MeshFilter.class).setMesh(mesh);
		box.addComponent(MeshRenderer.class).setMaterial(material);
		box.addComponent(BoxCollider.class).setSize(width, height, depth);

		return box;
	}

	public static void createArena(Arena arena, BindingInfo bindingInfo, Material material) {
		for (int x = 0; x < Arena.SIZE; x++) {
			for (int y = 0; y < Arena.SIZE; y++) {
				int tile = arena.getTile(x, y);

				GameObject box = createBox(Arena.TILE_SIZE, tile, Arena.TILE_SIZE, 1.0f, bindingInfo, material);

				box.getTransform().setPosition(new Vector3(x * Arena.TILE_SIZE, tile / 2.0f, y * Arena.TILE_SIZE));

				box.addComponent(RigidBody.class).setKinematic(true);
			}
		}

		/*
		GameObject box = createBox(Arena.SIZE * Arena.TILE_SIZE, 1.0f, Arena.SIZE * Arena.TILE_SIZE, 1.0f, bindingInfo, material);

		box.getTransform().setPosition(new Vector3((Arena.SIZE * Arena.TILE_SIZE) / 2.0f, Arena.HEIGHT, (Arena.SIZE * Arena.TILE_SIZE) / 2.0f));

		box.addComponent(RigidBody.class).setKinematic(true);
		*/
	}

}
