package zamtrax;

public abstract class Component {

	protected GameObject gameObject;
	protected Transform transform;
	private boolean enabled;

	public void onAdd() {
	}

	public void onRemove() {
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void update(float delta) {
	}

	public final GameObject getGameObject() {
		return gameObject;
	}

	void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
		transform = gameObject.getTransform();
	}

	public final Transform getTransform() {
		return transform;
	}

	public final <T extends Component> T getComponent(Class<T> componentClass) {
		return gameObject.getComponent(componentClass);
	}

	public final <T extends Component> T addComponent(Class<T> componentClass) {
		return gameObject.addComponent(componentClass);
	}

	public final boolean isEnabled() {
		return enabled && gameObject.isActive();
	}

	public final <T extends Component> T findComponentOfType(Class<T> componentClass) {
		return gameObject.findComponentOfType(componentClass);
	}

	public final <T extends Component> T findComponentInAncestorsOfType(Class<T> componentClass) {
		return gameObject.findComponentInAncestorsOfType(componentClass);
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
