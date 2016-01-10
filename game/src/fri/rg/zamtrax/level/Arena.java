package fri.rg.zamtrax.level;

import fri.rg.zamtrax.level.pathfinding.AStar;
import fri.rg.zamtrax.level.pathfinding.Agent;
import fri.rg.zamtrax.level.pathfinding.Map;
import fri.rg.zamtrax.level.pathfinding.Path;
import zamtrax.Vector3;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class Arena implements Map {

	public interface ChunkIterator {

		void accept(Chunk chunk, int x, int y, int z);

	}

	public static final int SIZE = 3 * 2 + 1;
	public static final int HEIGHT = 2;

	private Chunk[][][] chunks;

	public Arena() {
		chunks = new Chunk[SIZE][HEIGHT][SIZE];

		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				for (int z = 0; z < SIZE; z++) {
					chunks[x][y][z] = new Chunk();
				}
			}
		}
	}

	public void forEachChunk(ChunkIterator chunkIterator) {
		for (int x = 0; x < Arena.SIZE; x++) {
			for (int y = 0; y < Arena.HEIGHT; y++) {
				for (int z = 0; z < Arena.SIZE; z++) {
					chunkIterator.accept(getChunk(x, y, z), x, y, z);
				}
			}
		}
	}

	public Block getBlock(int x, int y, int z) {
		Chunk chunk = chunks[x / Chunk.SIZE][y / Chunk.SIZE][z / Chunk.SIZE];

		return chunk.getBlock(x % Chunk.SIZE, y % Chunk.SIZE, z % Chunk.SIZE);
	}

	public void setBlock(Block block, int x, int y, int z) {
		Chunk chunk = chunks[x / Chunk.SIZE][y / Chunk.SIZE][z / Chunk.SIZE];

		chunk.setBlock(block, x % Chunk.SIZE, y % Chunk.SIZE, z % Chunk.SIZE);
	}

	public Chunk getChunk(int x, int y, int z) {
		return chunks[x][y][z];
	}

	public Chunk[][][] getChunks() {
		return chunks;
	}

	@Override
	public int getWidth() {
		return SIZE * Chunk.SIZE;
	}

	@Override
	public int getHeight() {
		return SIZE * Chunk.SIZE;
	}

	@Override
	public boolean isBlocked(Agent agent, int x, int y) {
		return getBlock(x, 1, y) != Block.NULL;
	}

	@Override
	public float getCost(Agent agent, int sx, int sy, int tx, int ty) {
		return 1.0f;
	}

	public Path findPath(Agent agent, int sx, int sy, int tx, int ty) {
		return AStar.findPath(this, agent, sx, sy, tx, ty, false);
	}

}
