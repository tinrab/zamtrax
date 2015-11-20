package zamtrax;

public final class Camera extends Component {

	public enum ClearFlags {

		SOLID_COLOR, DEPTH, NOTHING

	}

	private static Camera mainCamera;

	public static void setMainCamera(Camera camera) {
		mainCamera = camera;
	}

	public static Camera getMainCamera() {
		return mainCamera;
	}

	private Matrix4 projection;
	private Transform transform;
	private ClearFlags clearFlags;
	private Color clearColor;

	@Override
	public void onAdd() {
		if (mainCamera == null) {
			mainCamera = this;
		}

		transform = getTransform();
		clearFlags = ClearFlags.SOLID_COLOR;
		clearColor = Color.createBlack();
	}

	public Matrix4 getProjectionMatrix() {
		Matrix4 r = transform.getRotation().conjugate().toMatrix();
		Vector3 p = transform.getPosition().mul(-1.0f);

		Matrix4 cameraTranslation = Matrix4.createTranslation(p.x, p.y, p.z);

		return projection.mul(r.mul(cameraTranslation));
	}

	public ClearFlags getClearFlags() {
		return clearFlags;
	}

	public void setClearFlags(ClearFlags clearFlags) {
		this.clearFlags = clearFlags;
	}

	public void setClearColor(Color color) {
		clearColor = color;
	}

	public Color getClearColor() {
		return clearColor;
	}

	public void setProjection(Matrix4 projection) {
		this.projection = projection;
	}

}
