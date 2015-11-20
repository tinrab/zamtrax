package zamtrax;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.*;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PhysicsModule extends Module implements Scene.Listener {

	private Map<zamtrax.RigidBody, RigidBody> rigidBodyMap;
	private DynamicsWorld dynamicsWorld;

	PhysicsModule(Scene scene) {
		super(scene);

		scene.addSceneListener(this);

		rigidBodyMap = new HashMap<>();

		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfig = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfig);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3f(0.0f, -9.81f, 0.0f));
	}

	@Override
	public void update(float delta) {
		dynamicsWorld.stepSimulation(delta);
	}

	@Override
	public void render() {
	}

	@Override
	public void dispose() {
		dynamicsWorld.destroy();
	}

	private void addRigidBody(zamtrax.RigidBody rigidBody) {
		MotionState ms = new DefaultMotionState(new Transform(new Matrix4f(rigidBody.getTransform().getRotation().toVecmath(),
				rigidBody.getTransform().getPosition().toVecmath(),
				1.0f)));

		CollisionShape collisionShape = rigidBody.getComponent(Collider.class).getCollisionShape();

		Vector3f intertia = new Vector3f(0.0f, 0.0f, 0.0f);
		collisionShape.calculateLocalInertia(rigidBody.getMass(), intertia);

		RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(rigidBody.getMass(), ms, collisionShape, intertia);
		info.restitution = 0.1f;
		info.angularDamping = 0.5f;

		RigidBody rb = new RigidBody(info);

		rigidBodyMap.put(rigidBody, rb);
		dynamicsWorld.addRigidBody(rb);

		rigidBody.linkRigidBody(rb);
	}

	private boolean removeRigidBody(zamtrax.RigidBody rigidBody) {
		RigidBody rb = rigidBodyMap.remove(rigidBody);

		if (rb == null) {
			return false;
		}

		dynamicsWorld.removeRigidBody(rb);

		return true;
	}

	@Override
	public void onCreateGameObject(GameObject gameObject) {
	}

	@Override
	public void onDestroyGameObject(GameObject gameObject) {
	}

	@Override
	public void onAddComponent(Component component) {
		if (component instanceof zamtrax.RigidBody) {
			addRigidBody((zamtrax.RigidBody) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof zamtrax.RigidBody) {
			removeRigidBody((zamtrax.RigidBody) component);
		}
	}

}
