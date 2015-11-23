package zamtrax;

import com.bulletphysics.collision.shapes.CollisionShape;

public abstract class Collider extends Component {

	protected com.bulletphysics.dynamics.RigidBody rigidBody;

	abstract CollisionShape getCollisionShape();

	void linkRigidBody(com.bulletphysics.dynamics.RigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}

	protected void updateShape() {
		if (rigidBody != null) {
			rigidBody.setCollisionShape(getCollisionShape());
		}
	}

}
