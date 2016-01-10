package zamtrax;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;

import javax.vecmath.Vector3f;

public final class Physics {

	private static Physics instance;

	public static Physics getInstance() {
		return instance;
	}

	private DiscreteDynamicsWorld dynamicsWorld;
	private Vector3f gravity;

	Physics(DiscreteDynamicsWorld dynamicsWorld) {
		instance = this;
		this.dynamicsWorld = dynamicsWorld;

		gravity = new Vector3f();
		gravity = dynamicsWorld.getGravity(gravity);
	}

	public RaycastHit raycast(Vector3 origin, Vector3 direction, float maxDistance) {
		return raycast(origin, origin.add(direction.mul(maxDistance)));
	}

	public RaycastHit raycast(Vector3 start, Vector3 end) {
		CollisionWorld.ClosestRayResultWithUserDataCallback rayResultCallback = new CollisionWorld.ClosestRayResultWithUserDataCallback(start.toVecmath(), end.toVecmath());

		dynamicsWorld.rayTest(start.toVecmath(), end.toVecmath(), rayResultCallback);

		if (rayResultCallback.hasHit()) {
			Vector3 hitPoint = new Vector3(rayResultCallback.hitPointWorld);
			Vector3 hitNormal = new Vector3(rayResultCallback.hitNormalWorld).normalized();
			float fraction = rayResultCallback.closestHitFraction;
			float distance = Vector3.distance(start, hitPoint);

			return new RaycastHit(hitPoint, hitNormal, distance, fraction, rayResultCallback.userData);
		}

		return null;
	}

	public Vector3 getGravity() {
		return new Vector3(gravity);
	}

	public void setGravity(Vector3 gravity) {
		this.gravity = gravity.toVecmath();

		dynamicsWorld.setGravity(this.gravity);
	}

}
