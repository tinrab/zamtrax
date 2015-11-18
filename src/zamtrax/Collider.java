package zamtrax;

import com.bulletphysics.collision.shapes.CollisionShape;

public abstract class Collider extends Component {

	abstract CollisionShape getCollisionShape();

}
