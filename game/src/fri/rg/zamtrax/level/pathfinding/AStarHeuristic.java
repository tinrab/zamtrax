package fri.rg.zamtrax.level.pathfinding;

public interface AStarHeuristic {

	float getCost(Map map, Agent agent, int x, int y, int tx, int ty);

}
