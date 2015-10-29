package zamtrax;

public abstract class SceneComponent {

	private SceneObject sceneObject;
	private boolean isEnabled;

	public void onAdd() {
	}

	public void onRemove() {
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void update() {
	}

	public final SceneObject getObject() {
		return sceneObject;
	}

	public final Transform getTransform() {
		return sceneObject.getTransform();
	}

	public final <T extends SceneComponent> SceneComponent getComponent(Class<T> componentClass) {
		return sceneObject.getComponent(componentClass);
	}

	public final boolean isEnabled() {
		return isEnabled;
	}

	public final void setEnabled(boolean enabled) {
		if (enabled && !isEnabled) {
			isEnabled = true;

			onEnable();
		} else if (!enabled && isEnabled) {
			isEnabled = false;

			onDisable();
		}
	}

	static <T extends SceneComponent> T create(Class<T> componentClass, SceneObject sceneObject) {
		try {
			T component = componentClass.newInstance();
			((SceneComponent) component).sceneObject = sceneObject;

			return component;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
