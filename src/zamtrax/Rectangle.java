package zamtrax;

public class Rectangle {

	public float x, y, width, height;

	public Rectangle() {
	}

	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean contains(Vector2 point) {
		return contains(point.x, point.y);
	}

	public boolean contains(float x, float y) {
		return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
	}

	@Override
	public String toString() {
		return String.format("Rectangle(%f, %f, %f, %f)", x, y, width, height);
	}

}
