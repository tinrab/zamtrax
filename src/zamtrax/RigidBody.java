package zamtrax;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

@RequireComponent(components = {Collider.class})
public final class RigidBody extends Component {

	private com.bulletphysics.dynamics.RigidBody bRigidBody;
	private com.bulletphysics.linearmath.Transform bTransform;
	private javax.vecmath.Quat4f bRotation;
	private javax.vecmath.Vector3f bPosition;

	private Transform transform;
	private Quaternion rotation;
	private Vector3 position;

	private float mass;
	private boolean dynamic;

	@Override
	public void onAdd() {
		mass = 1.0f;

		bTransform = new com.bulletphysics.linearmath.Transform();
		bRotation = new javax.vecmath.Quat4f();
		bPosition = new javax.vecmath.Vector3f();

		transform = getTransform();
		rotation = new Quaternion();
		position = new Vector3();
	}

	@Override
	public void update() {
		if (dynamic) {
			bRigidBody.getWorldTransform(bTransform);
			bTransform.getRotation(bRotation);
			bPosition = bTransform.origin;

			rotation.set(bRotation);
			position.set(bPosition);

			transform.setPosition(position);
			transform.setRotation(rotation);
		} else {
			bRigidBody.getMotionState().setWorldTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(
							transform.getRotation().toVecmath(),
							transform.getPosition().toVecmath(),
							1.0f)));
		}
	}

	public void addForce(Vector3 force) {
		addForce(force, ForceMode.FORCE);
	}

	public void addForce(Vector3 force, ForceMode forceMode) {
		switch (forceMode) {
			case FORCE:
				bRigidBody.applyCentralForce(force.toVecmath());
				break;
			case IMPULSE:
				bRigidBody.applyCentralImpulse(force.toVecmath());
				break;
			default:
				throw new UnsupportedOperationException(forceMode + " not supported yet");
		}
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;

		dynamic = mass != 0.0f;

		Vector3f intertia = new Vector3f();

		bRigidBody.getCollisionShape().calculateLocalInertia(mass, intertia);
		bRigidBody.setMassProps(mass, intertia);

		int flags = bRigidBody.getCollisionFlags();

		if (!dynamic) {
			flags |= CollisionFlags.KINEMATIC_OBJECT;
			bRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		}

		bRigidBody.setCollisionFlags(flags);
	}

	public boolean isDynamic() {
		return dynamic;
	}

	void linkRigidBody(com.bulletphysics.dynamics.RigidBody rigidBody) {
		bRigidBody = rigidBody;
	}

}
