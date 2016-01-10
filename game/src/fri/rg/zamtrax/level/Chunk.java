package fri.rg.zamtrax.level;

public class Chunk {

	public interface BlockIterator {

		void accept(Block block, int x, int y, int z);

	}

	public static final int SIZE = 4;
	public static final float BLOCK_SIZE = 1.5f;

	private Block[][][] blocks;

	public Chunk() {
		blocks = new Block[SIZE][SIZE][SIZE];

		forEachBlock((block, x, y, z) -> blocks[x][y][z] = Block.NULL);
	}

	public void forEachBlock(BlockIterator blockIterator) {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					blockIterator.accept(getBlock(x, y, z), x, y, z);
				}
			}
		}
	}

	public Block getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}

	public void setBlock(Block block, int x, int y, int z) {
		blocks[x][y][z] = block;
	}

}
