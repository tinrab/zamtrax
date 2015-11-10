package zamtrax;

import com.bulletphysics.collision.shapes.CollisionShape;

public abstract class Collider extends SceneComponent {

	abstract CollisionShape getCollisionShape();

}
