package zamtrax;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.character.KinematicCharacterController;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.*;
import com.bulletphysics.linearmath.Transform;
import zamtrax.components.CharacterController;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;

final class PhysicsModule extends Module implements Scene.Listener {

	private Map<zamtrax.components.RigidBody, RigidBody> rigidBodyMap;
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver constraintSolver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private DiscreteDynamicsWorld dynamicsWorld;

	PhysicsModule(Scene scene) {
		super(scene);

		scene.addSceneListener(this);

		rigidBodyMap = new HashMap<>();

		Vector3f worldMin = new Vector3f(-100f, -100f, -100f);
		Vector3f worldMax = new Vector3f(100f, 100f, 100f);
		AxisSweep3 sweepBP = new AxisSweep3(worldMin, worldMax);
		broadphase = sweepBP;

		sweepBP.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());

		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		constraintSolver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0.0f, -5.0f, 0.0f));

		new Physics(dynamicsWorld);
	}

	@Override
	public void update(float delta) {
		try {
			dynamicsWorld.stepSimulation(delta);
		} catch (Exception e) {
			// no idea
		}

		/*
		int numManifolds = dispatcher.getNumManifolds();
		Vector3f pointA = new Vector3f();
		Vector3f pointB = new Vector3f();

		for (int i = 0; i < numManifolds; i++) {
			PersistentManifold contactManifold = dispatcher.getManifoldByIndexInternal(i);

			CollisionObject objectA = (CollisionObject) contactManifold.getBody0();
			CollisionObject objectB = (CollisionObject) contactManifold.getBody1();

			int numContacts = contactManifold.getNumContacts();

			for (int j = 0; j < numContacts; j++) {
				ManifoldPoint pt = contactManifold.getContactPoint(j);

				if (pt.getDistance() < 0.0f) {
					pt.getPositionWorldOnA(pointA);
					pt.getPositionWorldOnB(pointB);
					Vector3f normal = pt.normalWorldOnB;


				}
			}
		}
		*/
	}

	@Override
	public void render() {
	}

	@Override
	public void dispose() {
		try {
			dynamicsWorld.destroy();
		} catch (Exception e) {
		}
	}

	private void addRigidBody(zamtrax.components.RigidBody rigidBody) {
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
		rb.setUserPointer(rigidBody);

		rigidBodyMap.put(rigidBody, rb);
		dynamicsWorld.addRigidBody(rb);

		rigidBody.linkRigidBody(rb);
		rigidBody.getComponent(Collider.class).linkRigidBody(rb);
	}

	private void addCharacterController(CharacterController characterController) {
		PairCachingGhostObject ghostObject = new PairCachingGhostObject();

		ghostObject.setWorldTransform(new Transform(new Matrix4f(characterController.getTransform().getRotation().toVecmath(),
				characterController.getTransform().getPosition().toVecmath(), 1.0f)));

		ConvexShape shape = new CapsuleShape(characterController.getRadius(), characterController.getHeight());

		ghostObject.setCollisionShape(shape);
		ghostObject.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
		ghostObject.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

		ghostObject.setRestitution(0.1f);

		ghostObject.setUserPointer(characterController);

		KinematicCharacterController kcc = new KinematicCharacterController(ghostObject, shape, characterController.getStepHeight());

		dynamicsWorld.addCollisionObject(ghostObject, CollisionFilterGroups.CHARACTER_FILTER, (short) (CollisionFilterGroups.STATIC_FILTER | CollisionFilterGroups.DEFAULT_FILTER));
		dynamicsWorld.addAction(kcc);

		characterController.setGhostObject(ghostObject);
		characterController.setKinematicCharacterController(kcc);
	}

	private boolean removeRigidBody(zamtrax.components.RigidBody rigidBody) {
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
		if (component instanceof zamtrax.components.RigidBody) {
			addRigidBody((zamtrax.components.RigidBody) component);
		} else if (component instanceof CharacterController) {
			addCharacterController((CharacterController) component);
		}
	}

	@Override
	public void onRemoveComponent(Component component) {
		if (component instanceof zamtrax.components.RigidBody) {
			removeRigidBody((zamtrax.components.RigidBody) component);
		}
	}

}
