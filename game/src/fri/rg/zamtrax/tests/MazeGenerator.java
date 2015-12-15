package fri.rg.zamtrax.tests;


import java.util.Collections;
import java.util.Arrays;

/*
 * recursive backtracking algorithm
 * shamelessly borrowed from the ruby at
 * http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
 */
public class MazeGenerator {
	private final int x;
	private final int y;
	private final int[][] maze;

	public MazeGenerator(int x, int y) {
		this.x = x;
		this.y = y;
		maze = new int[this.x][this.y];
		generateMaze(0, 0);
	}

	public void display() {
		for (int i = 0; i < y; i++) {
			// draw the north edge
			for (int j = 0; j < x; j++) {
				System.out.print((maze[j][i] & DIR.N.bit) == 0 ? "+---" : "+   ");
			}
			System.out.println("+");
			// draw the west edge
			for (int j = 0; j < x; j++) {
				System.out.print((maze[j][i] & DIR.W.bit) == 0 ? "|   " : "    ");
			}
			System.out.println("|");
		}
		// draw the bottom line
		for (int j = 0; j < x; j++) {
			System.out.print("+---");
		}
		System.out.println("+");
	}

	public void displayTiled() {
		boolean[][] tiles = new boolean[2 * x + 1][2 * y + 1];

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				int tx = i * 2 + 1;
				int ty = j * 2 + 1;
				int mask = maze[i][j];

				tiles[tx][ty] = true;

				if ((mask & DIR.N.bit) != 0) {
					tiles[tx][ty - 1] = true;
				}

				if ((mask & DIR.S.bit) != 0) {
					tiles[tx][ty + 1] = true;
				}

				if ((mask & DIR.E.bit) != 0) {
					tiles[tx + 1][ty] = true;
				}

				if ((mask & DIR.W.bit) != 0) {
					tiles[tx - 1][ty] = true;
				}
			}
		}

		for (int j = 0; j < tiles[0].length; j++) {
			for (int i = 0; i < tiles.length; i++) {
				System.out.print((tiles[i][j] ? " " : "X") + " ");
			}

			System.out.println();
		}
	}

	private void generateMaze(int cx, int cy) {
		DIR[] dirs = DIR.values();
		Collections.shuffle(Arrays.asList(dirs));

		for (DIR dir : dirs) {
			int nx = cx + dir.dx;
			int ny = cy + dir.dy;

			if (between(nx, x) && between(ny, y) && maze[nx][ny] == 0) {
				maze[cx][cy] |= dir.bit;
				maze[nx][ny] |= dir.opposite.bit;

				generateMaze(nx, ny);
			}
		}
	}

	private static boolean between(int v, int upper) {
		return (v >= 0) && (v < upper);
	}

	private enum DIR {
		N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
		private final int bit;
		private final int dx;
		private final int dy;
		private DIR opposite;

		static {
			N.opposite = S;
			S.opposite = N;
			E.opposite = W;
			W.opposite = E;
		}

		DIR(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}
	}

	public static void main(String[] args) {
		int x = 5;
		int y = 5;
		MazeGenerator maze = new MazeGenerator(x, y);
		maze.display();
		System.out.println("\n");
		maze.displayTiled();
	}

}