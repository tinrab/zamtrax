package fri.rg.zamtrax.level.pathfinding;

import zamtrax.Mathf;

public class ClosestHeuristic implements AStarHeuristic {

	@Override
	public float getCost(Map map, Agent agent, int x, int y, int tx, int ty) {
		float dx = tx - x;
		float dy = ty - y;

		return Mathf.sqrt(dx * dx + dy * dy);
	}

}
