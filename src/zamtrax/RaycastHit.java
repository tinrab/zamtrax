package zamtrax;

public class RaycastHit {

	private Vector3 point;
	private Vector3 normal;
	private float distance;
	private float fraction;
	private Object hitObject;

	RaycastHit(Vector3 point, Vector3 normal, float distance, float fraction, Object hitObject) {
		this.point = point;
		this.normal = normal;
		this.distance = distance;
		this.fraction = fraction;
		this.hitObject = hitObject;
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

	public Object getHitObject() {
		return hitObject;
	}

}
