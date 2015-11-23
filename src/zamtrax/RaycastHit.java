package zamtrax;

public class RaycastHit {

	private Vector3 point;
	private Vector3 normal;
	private float distance;
	private float fraction;

	public RaycastHit(Vector3 point, Vector3 normal, float distance, float fraction) {
		this.point = point;
		this.normal = normal;
		this.distance = distance;
	}

	public Vector3 getPoint() {
		return point;
	}

	public Vector3 getNormal() {
		return normal;
	}

	public float getDistance() {
		return distance;
	}

	public float getFraction() {
		return fraction;
	}

}
