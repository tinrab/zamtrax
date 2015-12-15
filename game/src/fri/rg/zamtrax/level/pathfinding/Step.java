package fri.rg.zamtrax.level.pathfinding;

public class Step {

	private int x, y;

	public Step(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x * y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Step)) {
			return false;
		}

		Step other = (Step) obj;

		return x == other.x && y == other.y;
	}

}