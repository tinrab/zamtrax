package fri.rg.zamtrax;

import zamtrax.Component;
import zamtrax.Vector3;

public class Rotate extends Component {

	private Vector3 angle;

	@Override
	public void update(float delta) {
		getTransform().rotate(angle.mul(delta));
	}

	public void setAngle(Vector3 angle) {
		this.angle = angle;
	}

}
