package fri.rg.zamtrax.level.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar implements PathFinder {

	private static final int MAX_SEARCH_DISTANCE = 100;

	private List<Node> closed;
	private List<Node> open;
	private Map map;
	private boolean allowDiagonalMovement;
	private Node[][] nodes;
	private AStarHeuristic heuristic;

	private AStar(Map map, boolean allowDiagonalMovement) {
		closed = new ArrayList<>();
		open = new ArrayList<>();
		this.map = map;
		this.allowDiagonalMovement = allowDiagonalMovement;
		heuristic = new ClosestHeuristic();

		nodes = new Node[map.getWidth()][map.getHeight()];

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				nodes[x][y] = new Node(x, y);
			}
		}
	}

	@Override
	public Path findPath(Agent agent, int sx, int sy, int tx, int ty) {
		if (map.isBlocked(agent, tx, ty)) {
			return null;
		}

		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);

		nodes[tx][ty].parent = null;

		int maxDepth = 0;

		while ((maxDepth < MAX_SEARCH_DISTANCE) && (open.size() != 0)) {
			Node current = getFirstOpen();

			if (current == nodes[tx][ty]) {
				break;
			}

			removeFromOpen(current);
			addToClosed(current);

			for (int x = -1; x < 2; x++) {
				for (int y = -1; y < 2; y++) {
					if ((x == 0) && (y == 0)) {
						continue;
					}

					if (!allowDiagonalMovement) {
						if ((x != 0) && (y != 0)) {
							continue;
						}
					}

					int xp = x + current.x;
					int yp = y + current.y;

					if (isValidLocation(agent, sx, sy, xp, yp)) {
						float nextStepCost = current.cost + map.getCost(agent, current.x, current.y, xp, yp);
						Node neighbour = nodes[xp][yp];

						if (nextStepCost < neighbour.cost) {
							if (inOpen(neighbour)) {
								removeFromOpen(neighbour);
							}

							if (inClosed(neighbour)) {
								removeFromClosed(neighbour);
							}
						}

						if (!inOpen(neighbour) && !(inClosed(neighbour))) {
							neighbour.cost = nextStepCost;
							neighbour.heuristic = heuristic.getCost(map, agent, xp, yp, tx, ty);
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));

							addToOpen(neighbour);
						}
					}
				}
			}
		}

		if (nodes[tx][ty].parent == null) {
			return null;
		}

		Path path = new Path();
		Node target = nodes[tx][ty];

		while (target != nodes[sx][sy]) {
			path.add(0, new Step(target.x, target.y));

			target = target.parent;
		}

		path.add(0, new Step(sx, sy));

		return path;
	}

	private Node getFirstOpen() {
		return open.get(0);
	}

	private void addToOpen(Node node) {
		open.add(node);
		Collections.sort(open);
	}

	private boolean inOpen(Node node) {
		return open.contains(node);
	}

	private void removeFromOpen(Node node) {
		open.remove(node);
	}

	private void addToClosed(Node node) {
		closed.add(node);
	}

	private boolean inClosed(Node node) {
		return closed.contains(node);
	}

	private void removeFromClosed(Node node) {
		closed.remove(node);
	}

	private boolean isValidLocation(Agent agent, int sx, int sy, int x, int y) {
		boolean invalid = x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight();

		if (!invalid && (sx != x || sy != y)) {
			invalid = map.isBlocked(agent, x, y);
		}

		return !invalid;
	}

	public static Path findPath(Map map, Agent agent, int sx, int sy, int tx, int ty, boolean allowDiagonalMovement) {
		AStar as = new AStar(map, allowDiagonalMovement);

		return as.findPath(agent, sx, sy, tx, ty);
	}

}
