package zamtrax.components;

import org.lwjgl.opengl.GL11;
import zamtrax.*;

public class Camera extends SceneComponent {

	private static Camera mainCamera;

	public static void setMainCamera(Camera camera) {
		mainCamera = camera;
	}

	public static Camera getMainCamera() {
		return mainCamera;
	}

	public enum ClearFlag {
		COLOR(GL11.GL_COLOR_BUFFER_BIT),
		DEPTH(GL11.GL_DEPTH_BUFFER_BIT),
		COLOR_AND_DEPTH(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT),
		NOTHING(GL11.GL_NONE);

		private final int value;

		ClearFlag(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	private Matrix4 projection;
	private Transform transform;
	private ClearFlag clearFlag;
	private Color clearColor;

	@Override
	public void onAdd() {
		if (mainCamera == null) {
			mainCamera = this;
		}

		transform = getTransform();
		clearFlag = ClearFlag.COLOR_AND_DEPTH;
		clearColor = Color.createBlack();
	}

	public Matrix4 getViewProjection() {
		Matrix4 r = transform.getTransformedRotation().conjugate().toMatrix();
		Vector3 p = transform.getTransformedPosition().mul(-1.0f);

		Matrix4 cameraTranslation = Matrix4.createTranslation(p.x, p.y, p.z);

		return projection.mul(r.mul(cameraTranslation));
	}

	public ClearFlag getClearFlag() {
		return clearFlag;
	}

	public void setClearFlag(ClearFlag clearFlag) {
		this.clearFlag = clearFlag;
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
