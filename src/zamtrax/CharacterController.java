package zamtrax;

import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.character.KinematicCharacterController;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public final class CharacterController extends Component {

	private com.bulletphysics.dynamics.RigidBody bRigidBody;
	private com.bulletphysics.linearmath.Transform bTransform;
	private javax.vecmath.Quat4f bRotation;
	private javax.vecmath.Vector3f bPosition;
	public KinematicCharacterController kinematicCharacterController;
	public PairCachingGhostObject ghostObject;

	private zamtrax.Transform transform;
	private float radius;
	private float height;
	private float stepHeight;

	@Override
	public void onAdd() {
		radius = 0.5f;
		height = 1.0f;
		stepHeight = 0.05f;

		transform = getTransform();

		bTransform = new Transform();
		bRotation = new Quat4f();
		bPosition = new Vector3f();
	}

	@Override
	public void update(float delta) {
		ghostObject.getWorldTransform(bTransform);

		bTransform.getRotation(bRotation);
		bPosition = bTransform.origin;

		transform.setRotation(new Quaternion(bRotation));
		transform.setPosition(new Vector3(bPosition));
	}

	public void move(Vector3 motion) {
		kinematicCharacterController.setWalkDirection(motion.toVecmath());
	}

	public void warp(Vector3 position) {
		kinematicCharacterController.warp(position.toVecmath());
	}

	public boolean isGrounded() {
		return kinematicCharacterController.onGround();
	}

	public void jump() {
		kinematicCharacterController.jump();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getStepHeight() {
		return stepHeight;
	}

	public void setStepHeight(float stepHeight) {
		this.stepHeight = stepHeight;
	}

	void setRigidBody(RigidBody rigidBody) {
		bRigidBody = rigidBody;
	}

	void setGhostObject(PairCachingGhostObject ghostObject) {
		this.ghostObject = ghostObject;
	}

	void setKinematicCharacterController(KinematicCharacterController kinematicCharacterController) {
		this.kinematicCharacterController = kinematicCharacterController;
	}

}
