package fri.rg.zamtrax.level;

import zamtrax.*;
import zamtrax.components.MeshCollider;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
import zamtrax.components.RigidBody;
import zamtrax.resources.*;

import java.util.Arrays;
import java.util.Collections;

public class ArenaFactory {

	private static final int SOUTH = 0;
	private static final int NORTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	private static final int TOP = 4;
	private static final int BOTTOM = 5;

	private enum Direction {

		NORTH(1, 0, -1), SOUTH(2, 0, 1), EAST(4, 1, 0), WEST(8, -1, 0);

		final int bit;
		final int dx, dy;
		Direction opposite;

		static {
			NORTH.opposite = SOUTH;
			SOUTH.opposite = NORTH;
			EAST.opposite = WEST;
			WEST.opposite = EAST;
		}

		Direction(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}

	}

	private static VoxelFace[][][] voxels = new VoxelFace[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];

	private static class VoxelFace {

		public Block block;
		public int side;

		public VoxelFace(Block block) {
			this.block = block;
		}

		public boolean equals(final VoxelFace face) {
			return face.block == this.block;
		}

	}

	public static void generate(Arena arena) {
		boolean[][] tiles = new boolean[Arena.SIZE][Arena.SIZE];
		int[][] maze = new int[(tiles.length - 1) / 2][(tiles[0].length - 1) / 2];

		generateMaze(maze, 0, 0);

		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				int tx = i * 2 + 1;
				int ty = j * 2 + 1;
				int m = maze[i][j];

				tiles[tx][ty] = true;

				if ((m & Direction.NORTH.bit) != 0) {
					tiles[tx][ty - 1] = true;
				}

				if ((m & Direction.SOUTH.bit) != 0) {
					tiles[tx][ty + 1] = true;
				}

				if ((m & Direction.EAST.bit) != 0) {
					tiles[tx + 1][ty] = true;
				}

				if ((m & Direction.WEST.bit) != 0) {
					tiles[tx - 1][ty] = true;
				}
			}
		}

		SimplexNoise simplex = new SimplexNoise(System.nanoTime());

		arena.forEachChunk((chunk, x, y, z) -> {
			if (y == 0) {
				fillBottom(arena, x, z, Block.BASIC, 1);

				if (!tiles[x][z]) {
					fillBottom(arena, x, z, Block.BASIC, (int) ((simplex.noise(x, z) * 0.5f + 0.5f) * Chunk.SIZE * 2));
				}
			}
		});

		for (int x = 0; x < Arena.SIZE * Chunk.SIZE; x++) {
			int y = Chunk.SIZE - 1 + Random.randomInteger(Chunk.SIZE);
			boolean inBounds = false;
			int lastZ = 0;

			for (int z = 0; z < Arena.SIZE * Chunk.SIZE; z++) {
				if (z - lastZ > Chunk.SIZE * 2) {
					inBounds = false;
				}

				if (arena.getBlock(x, y, z) != Block.NULL) {
					if (inBounds && arena.getBlock(x, y, z - 1) == Block.NULL) {
						inBounds = false;

						for (int zz = lastZ; zz <= z; zz++) {
							arena.setBlock(Block.BRIGHT, x, y, zz);
						}
					} else {
						inBounds = true;
						lastZ = z;
					}
				}
			}
		}

		for (int z = 0; z < Arena.SIZE * Chunk.SIZE; z++) {
			int y = Chunk.SIZE - 1 + Random.randomInteger(Chunk.SIZE);
			boolean inBounds = false;
			int lastX = 0;

			for (int x = 0; x < Arena.SIZE * Chunk.SIZE; x++) {
				if (x - lastX > Chunk.SIZE * 2) {
					inBounds = false;
				}

				if (arena.getBlock(x, y, z) != Block.NULL) {
					if (inBounds && arena.getBlock(x - 1, y, z) == Block.NULL) {
						inBounds = false;

						for (int xx = lastX; xx <= x; xx++) {
							arena.setBlock(Block.BRIGHT, xx, y, z);
						}
					} else {
						inBounds = true;
						lastX = x;
					}
				}
			}
		}

		for (int x = 1; x < Arena.SIZE * Chunk.SIZE - 1; x++) {
			for (int z = 1; z < Arena.SIZE * Chunk.SIZE - 1; z++) {
				for (int y = 1; y < Arena.HEIGHT * Chunk.SIZE - 1; y++) {
					if (Random.randomInteger(20) == 0) {
						if (arena.getBlock(x, y, z) == Block.NULL &&
								arena.getBlock(x - 1, y, z) != Block.NULL ||
								arena.getBlock(x + 1, y, z) != Block.NULL ||
								arena.getBlock(x, y - 1, z) != Block.NULL ||
								arena.getBlock(x, y + 1, z) != Block.NULL ||
								arena.getBlock(x, y, z - 1) != Block.NULL ||
								arena.getBlock(x, y, z + 1) != Block.NULL) {
							arena.setBlock(Block.BASIC, x, y, z);
						}
					}
				}
			}
		}
	}

	private static void fillBottom(Arena arena, int cx, int cz, Block block, int height) {
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				for (int y = 0; y < Arena.SIZE * Chunk.SIZE && y < height; y++) {
					arena.setBlock(block, cx * Chunk.SIZE + x, y, cz * Chunk.SIZE + z);
				}
			}
		}
	}

	private static void generateMaze(int[][] maze, int x, int y) {
		Direction[] dirs = Direction.values();

		Collections.shuffle(Arrays.asList(dirs));

		for (Direction d : dirs) {
			int nx = x + d.dx;
			int ny = y + d.dy;

			if ((nx >= 0 && nx < maze.length) && (ny >= 0 && ny < maze[0].length) && maze[nx][ny] == 0) {
				maze[x][y] |= d.bit;
				maze[nx][ny] |= d.opposite.bit;

				generateMaze(maze, nx, ny);
			}
		}
	}

	public static GameObject createMesh(Arena arena) {
		Material material = new Material("shaders/grid.vs", "shaders/grid.fs");
		material.setTexture("diffuse", Resources.loadTexture("textures/grid.png", Texture.Format.ARGB, Texture.WrapMode.REPEAT, Texture.FilterMode.NEAREST));

		GameObject arenaRoot = GameObject.create();

		arena.forEachChunk((chunk, x, y, z) -> {
			Mesh mesh = create(chunk);

			if (mesh.getVertices().size() > 0) {
				GameObject chunkObject = GameObject.create(arenaRoot);

				chunkObject.getTransform().setPosition(x * Chunk.SIZE * Chunk.BLOCK_SIZE, y * Chunk.SIZE * Chunk.BLOCK_SIZE, z * Chunk.SIZE * Chunk.BLOCK_SIZE);

				chunkObject.addComponent(MeshFilter.class).setMesh(mesh);
				chunkObject.addComponent(MeshRenderer.class).setMaterial(material);
				chunkObject.addComponent(MeshCollider.class);
				chunkObject.addComponent(RigidBody.class).setKinematic(true);
			}
		});

		return arenaRoot;
	}

	// Credit https://github.com/roboleary/GreedyMesh
	private static Mesh create(Chunk chunk) {
		Mesh.Builder meshBuilder = new Mesh.Builder();
		int indexOffset = 0;

		meshBuilder.setBindingInfo(new BindingInfo(AttributeType.POSITION, AttributeType.UV, AttributeType.COLOR, AttributeType.NORMAL));
		meshBuilder.calculateNormals();

		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				for (int y = 0; y < Chunk.SIZE; y++) {
					voxels[x][y][z] = new VoxelFace(chunk.getBlock(x, y, z));
				}
			}
		}

		int i, j, k, l, w, h, u, v, n, side = 0;

		final int[] x = new int[]{0, 0, 0};
		final int[] q = new int[]{0, 0, 0};
		final int[] du = new int[]{0, 0, 0};
		final int[] dv = new int[]{0, 0, 0};

		final VoxelFace[] mask = new VoxelFace[Chunk.SIZE * Chunk.SIZE];

		VoxelFace voxelFace, voxelFace1;

		for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {

			for (int d = 0; d < 3; d++) {
				u = (d + 1) % 3;
				v = (d + 2) % 3;

				x[0] = 0;
				x[1] = 0;
				x[2] = 0;

				q[0] = 0;
				q[1] = 0;
				q[2] = 0;
				q[d] = 1;

				if (d == 0) {
					side = backFace ? WEST : EAST;
				} else if (d == 1) {
					side = backFace ? BOTTOM : TOP;
				} else if (d == 2) {
					side = backFace ? SOUTH : NORTH;
				}

				for (x[d] = -1; x[d] < Chunk.SIZE; ) {

					n = 0;

					for (x[v] = 0; x[v] < Chunk.SIZE; x[v]++) {

						for (x[u] = 0; x[u] < Chunk.SIZE; x[u]++) {

							voxelFace = (x[d] >= 0) ? getVoxelFace(x[0], x[1], x[2], side) : null;
							voxelFace1 = (x[d] < Chunk.SIZE - 1) ? getVoxelFace(x[0] + q[0], x[1] + q[1], x[2] + q[2], side) : null;

							mask[n++] = ((voxelFace != null && voxelFace1 != null && voxelFace.equals(voxelFace1)))
									? null
									: backFace ? voxelFace1 : voxelFace;
						}
					}

					x[d]++;

					n = 0;

					for (j = 0; j < Chunk.SIZE; j++) {
						for (i = 0; i < Chunk.SIZE; ) {
							if (mask[n] != null) {

								for (w = 1; i + w < Chunk.SIZE && mask[n + w] != null && mask[n + w].equals(mask[n]); w++) {
								}

								boolean done = false;

								for (h = 1; j + h < Chunk.SIZE; h++) {

									for (k = 0; k < w; k++) {

										if (mask[n + k + h * Chunk.SIZE] == null || !mask[n + k + h * Chunk.SIZE].equals(mask[n])) {
											done = true;
											break;
										}
									}

									if (done) {
										break;
									}
								}

								if (mask[n].block != Block.NULL) {
									x[u] = i;
									x[v] = j;

									du[0] = 0;
									du[1] = 0;
									du[2] = 0;
									du[u] = w;

									dv[0] = 0;
									dv[1] = 0;
									dv[2] = 0;
									dv[v] = h;

									Vector3 bottomLeft = new Vector3(x[0], x[1], x[2]);
									Vector3 topLeft = new Vector3(x[0] + du[0], x[1] + du[1], x[2] + du[2]);
									Vector3 topRight = new Vector3(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]);
									Vector3 bottomRight = new Vector3(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]);

									VoxelFace voxel = mask[n];

									{
										Vertex v1 = new Vertex(bottomLeft.mul(Chunk.BLOCK_SIZE));
										Vertex v2 = new Vertex(bottomRight.mul(Chunk.BLOCK_SIZE));
										Vertex v3 = new Vertex(topLeft.mul(Chunk.BLOCK_SIZE));
										Vertex v4 = new Vertex(topRight.mul(Chunk.BLOCK_SIZE));

										v1.setColor(voxel.block.getColor());
										v2.setColor(voxel.block.getColor());
										v3.setColor(voxel.block.getColor());
										v4.setColor(voxel.block.getColor());

										v1.setUV(new Vector2(0.0f, 1).mul(h, w));
										v2.setUV(new Vector2(1, 1).mul(h, w));
										v3.setUV(new Vector2(0.0f, 0.0f).mul(h, w));
										v4.setUV(new Vector2(1, 0.0f).mul(h, w));

										meshBuilder.addVertices(v1, v2, v3, v4);

										if (backFace) {
											meshBuilder.addIndices(2 + indexOffset, 0 + indexOffset, 1 + indexOffset, 1 + indexOffset, 3 + indexOffset, 2 + indexOffset);
										} else {
											meshBuilder.addIndices(2 + indexOffset, 3 + indexOffset, 1 + indexOffset, 1 + indexOffset, 0 + indexOffset, 2 + indexOffset);
										}

										indexOffset += 4;
									}
								}

								for (l = 0; l < h; ++l) {

									for (k = 0; k < w; ++k) {
										mask[n + k + l * Chunk.SIZE] = null;
									}
								}

								i += w;
								n += w;
							} else {
								i++;
								n++;
							}
						}
					}
				}
			}
		}

		return meshBuilder.build();
	}

	private static VoxelFace getVoxelFace(final int x, final int y, final int z, final int side) {
		VoxelFace voxelFace = voxels[x][y][z];

		voxelFace.side = side;

		return voxelFace;
	}

}
