package zamtrax.components;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import zamtrax.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

@RequireComponent(components = {Collider.class})
public final class RigidBody extends Component {

	public static class ActivationStates {

		public static final int ACTIVE_TAG = CollisionObject.ACTIVE_TAG;
		public static final int ISLAND_SLEEPING = CollisionObject.ISLAND_SLEEPING;
		public static final int WANTS_DEACTIVATION = CollisionObject.WANTS_DEACTIVATION;
		public static final int DISABLE_DEACTIVATION = CollisionObject.DISABLE_DEACTIVATION;
		public static final int DISABLE_SIMULATION = CollisionObject.DISABLE_SIMULATION;

	}

	private com.bulletphysics.dynamics.RigidBody bRigidBody;
	private com.bulletphysics.linearmath.Transform bTransform;
	private javax.vecmath.Quat4f bRotation;
	private javax.vecmath.Vector3f bPosition;

	private Transform transform;

	private float mass;
	private boolean kinematic;

	@Override
	public void onAdd() {
		mass = 1.0f;

		bTransform = new com.bulletphysics.linearmath.Transform();
		bRotation = new javax.vecmath.Quat4f();
		bPosition = new javax.vecmath.Vector3f();

		transform = getTransform();
	}

	@Override
	public void update(float delta) {
		if (kinematic) {
			bTransform.set(new Matrix4f(
					transform.getRotation().toVecmath(),
					transform.getPosition().toVecmath(),
					1.0f));
			bRigidBody.setWorldTransform(bTransform);
		} else {
			bRigidBody.getWorldTransform(bTransform);
			bTransform.getRotation(bRotation);
			bPosition = bTransform.origin;

			transform.setPosition(new Vector3(bPosition));
			transform.setRotation(new Quaternion(bRotation));
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

		Vector3f intertia = new Vector3f();

		bRigidBody.getCollisionShape().calculateLocalInertia(mass, intertia);
		bRigidBody.setMassProps(mass, intertia);
	}

	public boolean isKinematic() {
		return kinematic;
	}

	public void setKinematic(boolean kinematic) {
		this.kinematic = kinematic;

		int flags = bRigidBody.getCollisionFlags();

		bTransform.set(new Matrix4f(
				transform.getRotation().toVecmath(),
				transform.getPosition().toVecmath(),
				1.0f));
		bRigidBody.setWorldTransform(bTransform);

		if (kinematic) {
			Vector3f intertia = new Vector3f();

			bRigidBody.getCollisionShape().calculateLocalInertia(0.0f, intertia);
			bRigidBody.setMassProps(0.0f, intertia);

			flags |= CollisionFlags.STATIC_OBJECT;
			bRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		} else {
			Vector3f intertia = new Vector3f();

			bRigidBody.getCollisionShape().calculateLocalInertia(mass, intertia);
			bRigidBody.setMassProps(mass, intertia);

			flags &= ~CollisionFlags.KINEMATIC_OBJECT;

			bRigidBody.setActivationState(CollisionObject.ISLAND_SLEEPING);
		}

		bRigidBody.setCollisionFlags(flags);
	}

	public void setAngularFactor(float angularFactor) {
		bRigidBody.setAngularFactor(angularFactor);
	}

	public void setActivationState(int activationState) {
		bRigidBody.setActivationState(activationState);
	}

	public Vector3 getLinearVelocity() {
		return new Vector3(bRigidBody.getLinearVelocity(new Vector3f()));
	}

	public void setLinearVelocity(Vector3 velocity) {
		bRigidBody.setLinearVelocity(velocity.toVecmath());
	}

	public void setGravity(Vector3 acceleration) {
		bRigidBody.setGravity(acceleration.toVecmath());
	}

	public void linkRigidBody(com.bulletphysics.dynamics.RigidBody rigidBody) {
		bRigidBody = rigidBody;
	}

}
