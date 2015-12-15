package fri.rg.zamtrax.level;

import zamtrax.Vector3;

import java.util.Arrays;
import java.util.Collections;

public class Arena {

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

	public static final int SIZE = 8 * 2 + 1;
	public static final int TILE_SIZE = 2;
	public static final int HEIGHT = 16;

	private int[][] tiles;
	private Vector3 spawnPoint;

	public Arena() {
		tiles = new int[SIZE][SIZE];
		spawnPoint = new Vector3(0.0f, 5.0f, 0.0f);

		generate();
	}

	public void generate() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				tiles[i][j] = 1;
			}
		}

		int[][] maze = new int[(SIZE - 1) / 2][(SIZE - 1) / 2];

		generateMaze(maze, 0, 0);

		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				int tx = i * 2 + 1;
				int ty = j * 2 + 1;
				int m = maze[i][j];

				tiles[tx][ty] = 0;

				if ((m & Direction.NORTH.bit) != 0) {
					tiles[tx][ty - 1] = 0;
				}

				if ((m & Direction.SOUTH.bit) != 0) {
					tiles[tx][ty + 1] = 0;
				}

				if ((m & Direction.EAST.bit) != 0) {
					tiles[tx + 1][ty] = 0;
				}

				if ((m & Direction.WEST.bit) != 0) {
					tiles[tx - 1][ty] = 0;
				}
			}
		}
	}

	private void generateMaze(int[][] maze, int x, int y) {
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

	public int getTile(int x, int y) {
		return tiles[x][y];
	}

	public Vector3 getSpawnPoint() {
		return spawnPoint;
	}

}
