package zamtrax;

public abstract class SceneComponent {

	private SceneObject sceneObject;
	private boolean enabled;

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

	void setObject(SceneObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	public final Transform getTransform() {
		return sceneObject.getTransform();
	}

	public final <T extends SceneComponent> T getComponent(Class<T> componentClass) {
		return sceneObject.getComponent(componentClass);
	}

	public final boolean isEnabled() {
		return enabled && sceneObject.isActive();
	}

	public final void setEnabled(boolean enabled) {
		if (enabled && !this.enabled) {
			this.enabled = true;

			onEnable();
		} else if (!enabled && this.enabled) {
			this.enabled = false;

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
