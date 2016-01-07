package zamtrax.components;

import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import zamtrax.Collider;

public final class CapsuleCollider extends Collider {

	private float radius;
	private float height;

	@Override
	public void onAdd() {
		radius = 1.0f;
		height = 2.0f;
	}

	@Override
	public CollisionShape getCollisionShape() {
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

}
