package zamtrax;

public abstract class Component {

	private GameObject gameObject;
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

	public final GameObject getGameObject() {
		return gameObject;
	}

	void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}

	public final Transform getTransform() {
		return gameObject.getTransform();
	}

	public final <T extends Component> T getComponent(Class<T> componentClass) {
		return gameObject.getComponent(componentClass);
	}

	public final boolean isEnabled() {
		return enabled && gameObject.isActive();
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

}