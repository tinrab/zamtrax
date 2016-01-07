package fri.rg.zamtrax.level;

import zamtrax.Vector3;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class Arena {

	public interface ChunkIterator {

		void accept(Chunk chunk, int x, int y, int z);

	}

	public static final int SIZE = 4 * 2 + 1;
	public static final int HEIGHT = 2;

	private Chunk[][][] chunks;
	private Vector3 spawnPoint;

	public Arena() {
		chunks = new Chunk[SIZE][HEIGHT][SIZE];

		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				for (int z = 0; z < SIZE; z++) {
					chunks[x][y][z] = new Chunk();
				}
			}
		}

		spawnPoint = new Vector3();
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

	public Vector3 getSpawnPoint() {
		return spawnPoint;
	}

}
