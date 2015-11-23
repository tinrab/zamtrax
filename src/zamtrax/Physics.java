package zamtrax;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.DynamicsWorld;

import javax.vecmath.Vector3f;

public final class Physics {

	private static Physics instance;

	public static Physics getInstance() {
		return instance;
	}

	private DynamicsWorld dynamicsWorld;
	private Vector3f gravity;

	Physics(DynamicsWorld dynamicsWorld) {
		instance = this;
		this.dynamicsWorld = dynamicsWorld;

		gravity = new Vector3f();
		gravity = dynamicsWorld.getGravity(gravity);
	}

	public RaycastHit raycast(Vector3 origin, Vector3 direction, float maxDistance) {
		return raycast(origin, new Vector3(origin).add(direction.mul(maxDistance)));
	}

	public RaycastHit raycast(Vector3 start, Vector3 end) {
		CollisionWorld.ClosestRayResultCallback rayResultCallback = new CollisionWorld.ClosestRayResultCallback(start.toVecmath(), end.toVecmath());

		dynamicsWorld.rayTest(start.toVecmath(), end.toVecmath(), rayResultCallback);

		if (rayResultCallback.hasHit()) {
			Vector3 hitPoint = new Vector3(rayResultCallback.hitPointWorld);
			Vector3 hitNormal = new Vector3(rayResultCallback.hitNormalWorld);
			float fraction = rayResultCallback.closestHitFraction;
			float distance = Vector3.distance(start, hitPoint);

			return new RaycastHit(hitPoint, hitNormal, distance, fraction);
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
