package zamtrax;

import java.util.ArrayList;
import java.util.List;

public final class GameObject {

	private GameObject parent;
	private Transform transform;
	private List<Component> components;
	private List<GameObject> children;
	private boolean active;

	GameObject() {
		transform = new Transform();
		components = new ArrayList<>();
		children = new ArrayList<>();
	}

	public <T extends Component> T addComponent(Class<T> componentClass) {
		return Game.getInstance().getCurrentScene().createComponent(componentClass, this);
	}

	public <T extends Component> T getComponent(Class<T> componentClass) {
		for (Component component : components) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				return (T) component;
			}
		}

		return null;
	}

	void addChild(GameObject gameObject) {
		children.add(gameObject);

		gameObject.parent = this;
		gameObject.getTransform().setParent(transform);
	}

	public void destroy() {
		setActive(false);

		Game.getInstance().getCurrentScene().destroyGameObject(this);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.active && !active) {
			this.active = false;

			components.forEach(Component::onDisable);
		} else if (!this.active && active) {
			this.active = true;

			components.forEach(Component::onEnable);
		}
	}

	public Transform getTransform() {
		return transform;
	}

	public GameObject getParent() {
		return parent;
	}

	void setParent(GameObject parent) {
		this.parent = parent;

		transform.setParent(parent.getTransform());
	}

	List<Component> getComponents() {
		return components;
	}

	List<GameObject> getChildren() {
		return children;
	}

	public static GameObject create() {
		return Game.getInstance().getCurrentScene().createGameObject(null);
	}

	public static GameObject create(GameObject parent) {
		return Game.getInstance().getCurrentScene().createGameObject(parent);
	}

}
