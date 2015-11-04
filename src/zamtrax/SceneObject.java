package zamtrax;

import java.util.ArrayList;
import java.util.List;

public final class SceneObject {

	private SceneObject parent;
	private Transform transform;
	private List<SceneComponent> components;
	private List<SceneObject> children;
	private boolean active;

	SceneObject() {
		transform = new Transform();
		components = new ArrayList<>();
		children = new ArrayList<>();
	}

	public <T extends SceneComponent> T addComponent(Class<T> componentClass) {
		T component = SceneComponent.create(componentClass, this);

		if (component == null) {
			throw new RuntimeException("Failed to add component");
		}

		components.add(component);

		if (component instanceof Renderer) {
			Game.getInstance().getCurrentScene().getRenderModule().addRenderer((Renderer) component);
		}

		component.onAdd();

		return component;
	}

	public <T extends SceneComponent> T getComponent(Class<T> componentClass) {
		for (SceneComponent component : components) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				return (T) component;
			}
		}

		return null;
	}

	void addChild(SceneObject sceneObject) {
		children.add(sceneObject);
		sceneObject.parent = this;
		sceneObject.getTransform().setParent(transform);
	}

	public void destroy() {
		setActive(false);

		Game.getInstance().getCurrentScene().destroy(this);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if(this.active && !active) {
			this.active = false;

			components.forEach(SceneComponent::onDisable);
		} else if(!this.active && active) {
			this.active = true;

			components.forEach(SceneComponent::onEnable);
		}
	}

	public Transform getTransform() {
		return transform;
	}

	public SceneObject getParent() {
		return parent;
	}

	void setParent(SceneObject parent) {
		this.parent = parent;
		transform.setParent(parent.getTransform());
	}

	List<SceneComponent> getComponents() {
		return components;
	}

	List<SceneObject> getChildren() {
		return children;
	}

	public static SceneObject create() {
		return create(Game.getInstance().getCurrentScene().getRoot());
	}

	public static SceneObject create(SceneObject parent) {
		if(parent == null) {
			parent = Game.getInstance().getCurrentScene().getRoot();
		}

		SceneObject sceneObject = new SceneObject();

		parent.addChild(sceneObject);

		return sceneObject;
	}

}
