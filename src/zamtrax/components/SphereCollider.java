package zamtrax.components;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import zamtrax.Collider;

public class SphereCollider extends Collider {

	private float radius;

	@Override
	public CollisionShape getCollisionShape() {
		return new SphereShape(radius);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;

		updateShape();
	}

}
