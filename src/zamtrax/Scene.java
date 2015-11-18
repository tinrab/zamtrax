package zamtrax;

import java.util.ArrayList;
import java.util.List;

public class Scene implements Disposable {

	public interface Listener {

		void onCreateGameObject(GameObject gameObject);

		void onDestroyGameObject(GameObject gameObject);

		void onAddComponent(Component component);

		void onRemoveComponent(Component component);

	}

	private GameObject root;
	private List<GameObject> destroyedObjects;
	private List<Listener> sceneListeners;

	private LogicModule logicModule;
	private PhysicsModule physicsModule;
	private RenderModule renderModule;

	protected Scene() {
		root = new GameObject();
		destroyedObjects = new ArrayList<>();
		sceneListeners = new ArrayList<>();

		logicModule = new LogicModule(this);
		physicsModule = new PhysicsModule(this);
		renderModule = new RenderModule(this);
	}

	public void onEnter() {
		logicModule.onCreate();
		physicsModule.onCreate();
		renderModule.onCreate();
	}

	public void onExit() {
	}

	final void update() {
		logicModule.update();
		physicsModule.update();
		renderModule.update();

		destroyObjects();
	}

	private void destroyObjects() {
		destroyedObjects.forEach(sceneObject -> sceneObject.getComponents().forEach(component -> {
			fireOnRemoveComponent(component);
			component.onRemove();
		}));

		destroyedObjects.forEach(gameObject -> {
			fireOnDestroyGameObject(gameObject);

			GameObject parent = gameObject.getParent();

			parent.getChildren().remove(gameObject);

			gameObject.getChildren().forEach(child -> parent.addChild(child));
		});

		destroyedObjects.clear();
	}

	final void render() {
		renderModule.render();
	}

	final void destroyGameObject(GameObject sceneObject) {
		destroyedObjects.add(sceneObject);
	}

	public GameObject createGameObject(GameObject parent) {
		if (parent == null) {
			parent = root;
		}

		GameObject gameObject = new GameObject();

		parent.addChild(gameObject);

		fireOnCreateGameObject(gameObject);

		return gameObject;
	}

	public <T extends Component> T createComponent(Class<T> componentClass, GameObject gameObject) {
		RequireComponent requireComponent = componentClass.getAnnotation(RequireComponent.class);
		List<Component> components = gameObject.getComponents();

		if (requireComponent != null) {
			for (Class requiredClass : requireComponent.components()) {
				if (components.stream().noneMatch(component -> requiredClass.isAssignableFrom(component.getClass()))) {
					throw new RuntimeException(componentClass.getSimpleName() + " requires " + requiredClass.getSimpleName() + " to be present");
				}
			}
		}

		T component = null;

		try {
			component = componentClass.newInstance();

			component.setGameObject(gameObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (component == null) {
			throw new RuntimeException("Failed to add component");
		}

		gameObject.getComponents().add(component);
		component.onAdd();

		fireOnAddComponent(component);

		return component;
	}

	public void addSceneListener(Listener sceneListener) {
		sceneListeners.add(sceneListener);
	}

	public boolean removeSceneListener(Listener sceneListener) {
		return sceneListeners.remove(sceneListener);
	}

	private void fireOnAddComponent(Component component) {
		sceneListeners.forEach(sceneListener -> sceneListener.onAddComponent(component));
	}

	private void fireOnRemoveComponent(Component component) {
		sceneListeners.forEach(sceneListener -> sceneListener.onRemoveComponent(component));
	}

	private void fireOnCreateGameObject(GameObject gameObject) {
		sceneListeners.forEach(sceneListener -> sceneListener.onCreateGameObject(gameObject));
	}

	private void fireOnDestroyGameObject(GameObject gameObject) {
		sceneListeners.forEach(sceneListener -> sceneListener.onDestroyGameObject(gameObject));
	}

	GameObject getRoot() {
		return root;
	}

	@Override
	public final void dispose() {
		logicModule.dispose();
		physicsModule.dispose();
		renderModule.dispose();
	}

}
