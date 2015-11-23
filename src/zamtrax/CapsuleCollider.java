package zamtrax;

import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;

public final class CapsuleCollider extends Collider {

	private float radius;
	private float height;

	@Override
	public void onAdd() {
		radius = 1.0f;
		height = 2.0f;
	}

	@Override
	CollisionShape getCollisionShape() {
		return new CapsuleShape(radius, height);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;

		updateShape();
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;

		updateShape();
	}

	void linkRigidBody(RigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}

}
