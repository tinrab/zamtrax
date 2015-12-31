package zamtrax;

public final class Transform extends Component {

	private Transform parent;
	private Matrix4 parentMatrix;
	private Vector3 position, oldPosition;
	private Quaternion rotation, oldRotation;
	private Vector3 scale, oldScale;

	private boolean changed;

	public Transform() {
		position = new Vector3();
		rotation = Quaternion.createIdentity();
		scale = new Vector3(1.0f, 1.0f, 1.0f);

		oldPosition = new Vector3();
		oldRotation = Quaternion.createIdentity();
		oldScale = new Vector3(1.0f, 1.0f, 1.0f);

		parentMatrix = Matrix4.createIdentity();
		changed = true;
	}

	public void update() {
		changed = (parent != null && parent.hasChanged()) || !position.equals(oldPosition) || !rotation.equals(oldRotation) || !scale.equals(oldScale);

		oldPosition.set(position);
		oldRotation.set(rotation);
		oldScale.set(scale);
	}

	public boolean hasChanged() {
		return changed;
	}

	public void translate(Vector3 translation) {
		translate(translation, Space.SELF);
	}

	public void translate(Vector3 translation, Space relativeTo) {
		translate(translation.x, translation.y, translation.z, relativeTo);
	}

	public void translate(float x, float y, float z) {
		translate(x, y, z, Space.SELF);
	}

	public void translate(float x, float y, float z, Space space) {
		switch (space) {
			case WORLD:
				position.set(position.add(x, y, z));
				break;
			case SELF:
				position.set(position.add(rotation.rotatePoint(new Vector3(x, y, z))));
				break;
		}
	}

	public void rotate(float x, float y, float z) {
		rotate(x, y, z, Space.WORLD);
	}

	public void rotate(float x, float y, float z, Space space) {
		rotate(new Vector3(x, y, z), space);
	}

	public void rotate(Vector3 eulerAngles) {
		rotate(eulerAngles, Space.WORLD);
	}

	public void rotate(Vector3 eulerAngles, Space space) {
		rotate(Quaternion.fromEuler(eulerAngles), space);
	}

	public void rotate(Vector3 axis, float angle) {
		rotate(axis, angle, Space.WORLD);
	}

	public void rotate(Vector3 axis, float angle, Space space) {
		rotate(Quaternion.fromAxisAngle(axis, angle), space);
	}

	public void rotate(Quaternion rotation, Space space) {
		switch (space) {
			case WORLD:
				this.rotation.set(rotation.mul(this.rotation).normalized());
				break;
			case SELF:
				this.rotation.set(this.rotation.mul(rotation).normalized());
				break;
		}
	}

	Transform getParent() {
		return parent;
	}

	void setParent(Transform parent) {
		setParent(parent, false);
	}

	void setParent(Transform parent, boolean worldPositionStays) {
		if (worldPositionStays) {
			position = parent.getLocalToWorldMatrix().inverse().transformPoint(position);
			rotation = parent.getRotation().inverse().mul(rotation);
			scale = scale.div(parent.getScale());
		}

		this.parent = parent;
	}

	public Vector3 getPosition() {
		return getParentMatrix().transformPoint(position);
	}

	public void setPosition(Vector3 position) {
		this.position.set(getParentMatrix().inverse().transformPoint(position));
	}

	public Vector3 getLocalPosition() {
		return new Vector3(position);
	}

	public void setLocalPosition(Vector3 position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		if (parent != null) {
			return parent.getRotation().mul(rotation);
		}

		return new Quaternion(rotation);
	}

	public void setRotation(Quaternion rotation) {
		this.rotation.set(parent.getRotation().inverse().mul(rotation));
	}

	public Quaternion getLocalRotation() {
		return new Quaternion(rotation);
	}

	public void setLocalRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Vector3 getScale() {
		if (parent != null) {
			parent.getScale().mul(scale);
		}

		return new Vector3(scale);
	}

	public void setScale(Vector3 scale) {
		this.scale.set(scale.div(parent.getScale()));
	}

	public Vector3 getLocalScale() {
		return new Vector3(scale);
	}

	public void setLocalScale(Vector3 scale) {
		this.scale = scale;
	}

	public Matrix4 getWorldToLocalMatrix() {
		// TODO
		return null;
	}

	public Matrix4 getLocalToWorldMatrix() {
		Matrix4 t = Matrix4.createTranslation(position);
		Matrix4 r = rotation.toMatrix();
		Matrix4 s = Matrix4.createScale(scale);

		return getParentMatrix().mul(t.mul(r.mul(s)));
	}

	private Matrix4 getParentMatrix() {
		if (parent != null && parent.hasChanged()) {
			parentMatrix = parent.getLocalToWorldMatrix();
		}

		return parentMatrix;
	}

	public Vector3 forward() {
		return getRotation().rotatePoint(Vector3.FORWARD);
	}

	public Vector3 back() {
		return getRotation().rotatePoint(Vector3.BACK);
	}

	public Vector3 left() {
		return getRotation().rotatePoint(Vector3.LEFT);
	}

	public Vector3 right() {
		return getRotation().rotatePoint(Vector3.RIGHT);
	}

	public Vector3 up() {
		return getRotation().rotatePoint(Vector3.UP);
	}

	public Vector3 down() {
		return getRotation().rotatePoint(Vector3.DOWN);
	}

}
