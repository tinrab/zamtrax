package zamtrax;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

public final class BoxCollider extends Collider {

	private Vector3 size;

	@Override
	public void onAdd() {
		size = new Vector3(1.0f, 1.0f, 1.0f);
	}

	@Override
	CollisionShape getCollisionShape() {
		Vector3 halfExtents = size.div(2.0f);

		return new BoxShape(halfExtents.toVecmath());
	}

	public Vector3 getSize() {
		return size;
	}

	public void setSize(Vector3 size) {
		this.size = size;

		updateShape();
	}

	public void setSize(float x, float y, float z) {
		size.set(x, y, z);

		updateShape();
	}

}
