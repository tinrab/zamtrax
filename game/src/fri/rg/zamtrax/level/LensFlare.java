package fri.rg.zamtrax.level;

import fri.rg.zamtrax.FlareFilter;
import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.CharacterController;

public class LensFlare extends Component {

	private FlareFilter filter;
	private Transform camera;
	private float brightness;

	@Override
	public void onAdd() {
		super.onAdd();

		filter = new FlareFilter();

		filter.setWorldPosition(new Vector3(Vector3.FORWARD).rotate(Quaternion.fromEuler(-50, -45, -10)).mul(1000.0f));

		camera = Camera.getMainCamera().getTransform();
		brightness = filter.getBrightness();
	}

	public FlareFilter getFilter() {
		return filter;
	}

	@Override
	public void update(float delta) {
		RaycastHit hit = Physics.getInstance().raycast(filter.getWorldPosition(), camera.getPosition());

		if (hit != null && hit.getHitObject() instanceof CharacterController) {
			brightness = Mathf.lerp(brightness, 1.0f, delta * 20.0f);
		} else {
			brightness = Mathf.lerp(brightness, 0.0f, delta * 20.0f);
		}

		filter.setBrightness(brightness);
	}

}
