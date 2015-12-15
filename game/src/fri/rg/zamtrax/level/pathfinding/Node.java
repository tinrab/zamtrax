package fri.rg.zamtrax.level.pathfinding;

public class Node implements Comparable<Node> {

	public int x, y;
	public float cost, heuristic;
	public Node parent;
	public int depth;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int setParent(Node parent) {
		this.parent = parent;
		depth = parent.depth + 1;

		return depth;
	}

	@Override
	public int compareTo(Node other) {
		float f = heuristic + cost;
		float of = other.heuristic + other.cost;

		if (f < of) {
			return -1;
		}

		if (f > of) {
			return 1;
		}

		return 0;
	}

}
