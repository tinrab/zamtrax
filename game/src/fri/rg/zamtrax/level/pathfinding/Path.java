package fri.rg.zamtrax.level.pathfinding;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private List<Step> steps;

	public Path() {
		steps = new ArrayList<>();
	}

	public void add(Step step) {
		steps.add(step);
	}

	public void add(int index, Step step) {
		steps.add(index, step);
	}

	public boolean contains(int x, int y) {
		return steps.stream().anyMatch(step -> step.getX() == x && step.getY() == y);
	}

	public int getX(int index) {
		return steps.get(index).getX();
	}

	public int getY(int index) {
		return steps.get(index).getY();
	}

	public int getLength() {
		return steps.size();
	}

}
