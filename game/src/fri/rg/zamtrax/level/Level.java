package fri.rg.zamtrax.level;

import fri.rg.zamtrax.FlareFilter;
import fri.rg.zamtrax.Rotate;
import fri.rg.zamtrax.level.enemies.EnemySpawner;
import fri.rg.zamtrax.level.player.FreeLook;
import fri.rg.zamtrax.level.player.Player;
import zamtrax.*;
import zamtrax.components.*;
import zamtrax.resources.Material;
import zamtrax.resources.Mesh;
import zamtrax.resources.Texture;

import java.util.ArrayList;
import java.util.List;

public class Level extends Scene {

	private static Level instance;

	public static Level getInstance() {
		return instance;
	}

	private Arena arena;
	private List<Node> nodes;

	@Override
	public void onEnter() {
		super.onEnter();

		instance = this;

		arena = new Arena();

		setAmbientLight(new Color(0.4f, 0.4f, 0.4f));

		{
			DirectionalLight dl1 = GameObject.create().addComponent(DirectionalLight.class);

			dl1.getTransform().setRotation(Quaternion.fromEuler(-60, -45, -10));
			dl1.setColor(new Color(0.5f, 0.85f, 1.0f));
			dl1.setIntensity(0.7f);
			dl1.setShadows(Light.Shadows.HARD);

			DirectionalLight dl2 = GameObject.create().addComponent(DirectionalLight.class);

			dl2.getTransform().setRotation(Quaternion.fromEuler(-60, 135, -10));
			dl2.setColor(new Color(0.9f, 0.85f, 0.5f));
			dl2.setIntensity(0.7f);
		}

		GameObject.create().addComponent(Player.class);
		/*
		Transform fl = GameObject.create().addComponent(FreeLook.class).getTransform();
		fl.setPosition(new Vector3(0, 5, 0));
		fl.setRotation(Quaternion.fromEuler(new Vector3(30, 0, 0).mul(Mathf.DEG_TO_RAD)));
		*/

		{
			ArenaFactory.generate(arena);
			ArenaFactory.createMesh(arena);

			nodes = new ArrayList<>();

			Mesh nodeMesh = Resources.loadModel("models/node.ply");
			Mesh nodeTopMesh = Resources.loadModel("models/node_top.ply");
			Material nodeMaterial = new Material("shaders/vertexColor.vs", "shaders/vertexColor.fs");

			for (int i = 0; i < 3; i++) {
				GameObject node = GameObject.create();
				node.addComponent(MeshFilter.class).setMesh(nodeMesh);
				node.addComponent(MeshRenderer.class).setMaterial(nodeMaterial);
				node.getTransform().setPosition(findNodePosition());

				GameObject nodeTop = GameObject.create(node);
				nodeTop.addComponent(MeshFilter.class).setMesh(nodeTopMesh);
				nodeTop.addComponent(MeshRenderer.class).setMaterial(nodeMaterial);
				nodeTop.getTransform().setLocalPosition(0.0f, 0.0f, 0.0f);

				nodes.add(node.addComponent(Node.class));
			}
		}

		GameObject.create().addComponent(EnemySpawner.class);
		GameObject.create().addComponent(HUD.class);

		{
			GameObject skyDome = GameObject.create();
			Material material = new Material("shaders/skyDome.vs", "shaders/skyDome.fs");
			material.setTexture("diffuse", Resources.loadTexture("textures/stars.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));
			skyDome.addComponent(MeshFilter.class).setMesh(Resources.loadModel("models/skyDome.ply"));
			skyDome.addComponent(MeshRenderer.class).setMaterial(material);
		}

		addScreenFilter(Player.getInstance().getHurtFilter());
		addScreenFilter(GameObject.create().addComponent(LensFlare.class).getFilter());
	}

	private Vector3 findNodePosition() {
		int x = 0, y = 1, z = 0;

		while (true) {
			x = Random.randomInteger(Arena.SIZE * Chunk.SIZE - 2) + 1;
			z = Random.randomInteger(Arena.SIZE * Chunk.SIZE - 2) + 1;

			if (arena.getBlock(x, y, z) == Block.NULL && (
					arena.getBlock(x - 1, y, z) == Block.NULL &&
							arena.getBlock(x + 1, y, z) == Block.NULL &&
							arena.getBlock(x, y, z - 1) == Block.NULL &&
							arena.getBlock(x, y, z + 1) == Block.NULL)) {
				break;
			}
		}

		/*
		do {
			x = Random.randomInteger(Arena.SIZE * Chunk.SIZE - 2) + 1;
			z = Random.randomInteger(Arena.SIZE * Chunk.SIZE - 2) + 1;
		} while (arena.getBlock(x, y, z) != Block.NULL && (
				arena.getBlock(x - 1, y, z) != Block.NULL &&
						arena.getBlock(x + 1, y, z) != Block.NULL &&
						arena.getBlock(x, y, z - 1) != Block.NULL &&
						arena.getBlock(x, y, z + 1) != Block.NULL));
		*/

		return new Vector3(x, y, z).add(0.5f, 0.0f, 0.5f).mul(Chunk.BLOCK_SIZE);
	}

	public Vector3 getFreeLocation() {
		int x = 0, y = 1, z = 0;

		while (true) {
			x = Random.randomInteger(Arena.SIZE * Chunk.SIZE - 2) + 1;
			z = Random.randomInteger(Arena.SIZE * Chunk.SIZE - 2) + 1;

			if (arena.getBlock(x, y, z) == Block.NULL) {
				break;
			}
		}

		return new Vector3(x, y, z).add(0.5f, 0.0f, 0.5f).mul(Chunk.BLOCK_SIZE);
	}

	public Arena getArena() {
		return arena;
	}

	public List<Node> getNodes() {
		return nodes;
	}

}

