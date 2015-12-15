package fri.rg.zamtrax.level.pathfinding;

public interface Map {

	int getWidth();

	int getHeight();

	boolean isBlocked(Agent agent, int x, int y);

	float getCost(Agent agent, int sx, int sy, int tx, int ty);

}
