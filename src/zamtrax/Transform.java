package zamtrax;

public class Transform {

	private Transform parent;
	private Vector3 position;
	private Quaternion rotation;
	private Vector3 scale;

	Transform() {
		position = new Vector3();
		rotation = Quaternion.createIdentity();
		scale = new Vector3(1.0f, 1.0f, 1.0f);
	}

	public void rotate(Vector3 axis, float angle) {
		rotation = new Quaternion(axis, angle).mul(rotation).normalized();
	}

	public void lookAt(Vector3 point, Vector3 up) {
		rotation = getLookAtRotation(point, up);
	}

	public Quaternion getLookAtRotation(Vector3 point, Vector3 up) {
		return new Quaternion(Matrix4.createRotation(point.sub(position).normalized(), up));
	}

	public void translate(Vector3 translation) {
		translate(translation, Space.WORLD);
	}

	public void translate(Vector3 translation, Space space) {
		translate(translation.x, translation.y, translation.z, space);
	}

	public void translate(float x, float y, float z) {
		translate(x, y, z, Space.WORLD);
	}

	public void translate(float x, float y, float z, Space space) {
		switch (space) {
			case WORLD:
				position = position.add(x, y, z);
				break;
			case LOCAL:
				// TODO
				break;
		}
	}

	public Vector3 getScale() {
		return scale;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	public Transform getParent() {
		return parent;
	}

	void setParent(Transform parent) {
		this.parent = parent;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Matrix4 getTransformation() {
		Matrix4 t = Matrix4.createTranslation(position);
		Matrix4 r = rotation.toMatrix();
		Matrix4 s = Matrix4.createScale(scale);

		Matrix4 result = t.mul(r.mul(s));

		if (parent != null) {
			result = parent.getTransformation().mul(result);
		}

		return result;
	}

	public Vector3 getTransformedPosition() {
		if (parent != null) {
			parent.getTransformation().transformPoint(position);
		}

		return position;
	}

	public Quaternion getTransformedRotation() {
		Quaternion parentRotation = Quaternion.createIdentity();

		if (parent != null) {
			parentRotation = parent.getTransformedRotation();
		}

		return parentRotation.mul(rotation);
	}

}
