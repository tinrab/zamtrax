package zamtrax;

import com.bulletphysics.collision.shapes.CollisionShape;
import zamtrax.Component;

public abstract class Collider extends Component {

	protected com.bulletphysics.dynamics.RigidBody rigidBody;

	public abstract CollisionShape getCollisionShape();

	public void linkRigidBody(com.bulletphysics.dynamics.RigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}

	protected void updateShape() {
		if (rigidBody != null) {
			rigidBody.setCollisionShape(getCollisionShape());
		}
	}

}
