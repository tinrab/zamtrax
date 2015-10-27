import zamtrax.SceneComponent;
import zamtrax.Time;
import zamtrax.Transform;
import zamtrax.Vector3;

public class Rotate extends SceneComponent {

	private Vector3 axis;
	private float speed;
	private Transform transform;

	@Override
	public void onCreate() {
		axis = new Vector3(1.0f, 0.0f, 0.0f);
		speed = 50.0f;
		transform = getTransform();
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void update() {
		transform.rotate(axis, speed * Time.getDeltaTime());
	}

	public void setAxis(Vector3 axis) {
		this.axis = axis;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
