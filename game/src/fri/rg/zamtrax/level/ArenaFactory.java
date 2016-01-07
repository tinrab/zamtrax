package fri.rg.zamtrax.level;

import zamtrax.*;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
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

	private static final int VOXEL_SIZE = 1;

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
		int[][] tiles = new int[Arena.SIZE][Arena.SIZE];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = Chunk.SIZE * Arena.HEIGHT;
			}
		}

		int[][] maze = new int[(tiles.length - 1) / 2][(tiles[0].length - 1) / 2];

		generateMaze(maze, 0, 0);

		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				int tx = i * 2 + 1;
				int ty = j * 2 + 1;
				int m = maze[i][j];

				tiles[tx][ty] = 1;

				if ((m & Direction.NORTH.bit) != 0) {
					tiles[tx][ty - 1] = 1;
				}

				if ((m & Direction.SOUTH.bit) != 0) {
					tiles[tx][ty + 1] = 1;
				}

				if ((m & Direction.EAST.bit) != 0) {
					tiles[tx + 1][ty] = 1;
				}

				if ((m & Direction.WEST.bit) != 0) {
					tiles[tx - 1][ty] = 1;
				}
			}
		}

		// wals
		for (int i = 0; i < tiles.length; i++) {
			tiles[i][0] = Arena.HEIGHT * Chunk.SIZE;
			tiles[i][tiles.length - 1] = Arena.HEIGHT * Chunk.SIZE;
			tiles[0][i] = Arena.HEIGHT * Chunk.SIZE;
			tiles[tiles.length - 1][i] = Arena.HEIGHT * Chunk.SIZE;
		}

		SimplexNoise simplex = new SimplexNoise(1337);
		float scale = 0.01f;

		arena.forEachChunk((chunk, x, y, z) -> {
			int tile = (int) (tiles[x][z] * simplex.noise(x, z)) - Chunk.SIZE * y;

			if (tile < 1) {
				tile = 1;
			}

			for (int bx = 0; bx < Chunk.SIZE; bx++) {
				for (int by = 0; by < Chunk.SIZE; by++) {
					for (int bz = 0; bz < Chunk.SIZE; bz++) {
						if (by >= tile || ((bx == 0 || bz == 0 || bx == Chunk.SIZE - 1 || bz == Chunk.SIZE - 1) && by >= tile || by == tile - 1 && simplex.noise(bx + x, by + y, bz + z) > 0.5f)) {
							chunk.setBlock(Block.NULL, bx, by, bz);
						} else {
							chunk.setBlock(Block.BASIC, bx, by, bz);
						}
					}
				}
			}
		});
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

		Chunk[][][] chunks = arena.getChunks();
		GameObject arenaRoot = GameObject.create();

		arena.forEachChunk((chunk, x, y, z) -> {
			GameObject chunkObject = GameObject.create(arenaRoot);

			chunkObject.addComponent(MeshFilter.class).setMesh(create(chunk));
			chunkObject.addComponent(MeshRenderer.class).setMaterial(material);

			chunkObject.getTransform().setPosition(x * Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE);
		});

		return arenaRoot;
	}

	// Credit https://github.com/roboleary/GreedyMesh
	private static Mesh create(Chunk chunk) {
		Mesh.Builder meshBuilder = new Mesh.Builder();
		int indexOffset = 0;

		meshBuilder.setBindingInfo(new BindingInfo(AttributeType.POSITION, AttributeType.COLOR, AttributeType.NORMAL));
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
										Vertex v1 = new Vertex(bottomLeft.mul(VOXEL_SIZE));
										Vertex v2 = new Vertex(bottomRight.mul(VOXEL_SIZE));
										Vertex v3 = new Vertex(topLeft.mul(VOXEL_SIZE));
										Vertex v4 = new Vertex(topRight.mul(VOXEL_SIZE));

										v1.setColor(voxel.block.getColor());
										v2.setColor(voxel.block.getColor());
										v3.setColor(voxel.block.getColor());
										v4.setColor(voxel.block.getColor());

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
