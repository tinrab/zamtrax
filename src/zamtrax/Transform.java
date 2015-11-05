package zamtrax;

public class Transform {

	private Transform parent;
	private Matrix4 parentMatrix;
	private Vector3 position, oldPosition;
	private Quaternion rotation, oldRotation;
	private Vector3 scale, oldScale;

	private boolean changed;

	Transform() {
		position = new Vector3();
		rotation = Quaternion.createIdentity();
		scale = new Vector3(1.0f, 1.0f, 1.0f);

		oldPosition = new Vector3();
		oldRotation = Quaternion.createIdentity();
		oldScale = new Vector3(1.0f, 1.0f, 1.0f);

		parentMatrix = Matrix4.createIdentity();
	}

	void update() {
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
				break;
			case SELF:
				position = position.add(x, y, z);
				break;
		}
	}

	public void rotate(float x, float y, float z) {
		rotate(x, y, z, Space.SELF);
	}

	public void rotate(float x, float y, float z, Space relativeTo) {
		rotate(new Vector3(x, y, z), relativeTo);
	}

	public void rotate(Vector3 eulerAngles) {
		rotate(eulerAngles, Space.SELF);
	}

	public void rotate(Vector3 eulerAngles, Space relativeTo) {
		rotate(Quaternion.fromEuler(eulerAngles), relativeTo);
	}

	public void rotate(Vector3 axis, float angle) {
		rotate(axis, angle, Space.SELF);
	}

	public void rotate(Vector3 axis, float angle, Space relativeTo) {
		rotate(Quaternion.fromAxisAngle(axis, angle), relativeTo);
	}

	public void rotate(Quaternion rotation, Space relativeTo) {
		switch (relativeTo) {
			case WORLD:
				//TODO
				break;
			case SELF:
				this.rotation.set(rotation.mul(this.rotation).normalized());
				break;
		}
	}

	public void lookAt(Vector3 point, Vector3 up) {
		rotation = getLookAtRotation(point, up);
	}

	public Quaternion getLookAtRotation(Vector3 point, Vector3 up) {
		return new Quaternion(Matrix4.createRotation(point.sub(position).normalized(), up));
	}

	public Transform getParent() {
		return parent;
	}

	void setParent(Transform parent) {
		this.parent = parent;
	}

	public Vector3 getPosition() {
		if (parent != null) {
			return parent.getLocalToWorldMatrix().transformPoint(position);
		}

		return new Vector3(position);
	}

	public void setPosition(Vector3 position) {
		// TODO
		this.position = position;
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
		// TODO
		this.rotation = rotation;
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
		// TODO
		this.scale = scale;
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
