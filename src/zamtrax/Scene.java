package zamtrax;

import java.util.ArrayList;
import java.util.List;

public class Scene implements Disposable {

	private SceneObject root;
	private List<SceneObject> destroyedObjects;

	private LogicModule logicModule;
	private PhysicsModule physicsModule;
	private RenderModule renderModule;

	protected Scene() {
		root = new SceneObject();
		destroyedObjects = new ArrayList<>();

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

		// TODO make this better
		destroyedObjects.forEach(sceneObject -> {
			sceneObject.getComponents().forEach(SceneComponent::onRemove);

			sceneObject.getComponents().stream().filter(component -> component instanceof Renderer).forEach(component -> {
				Renderer renderer = (Renderer) component;

				Game.getInstance().getCurrentScene().getRenderModule().removeRenderer(renderer);
			});
		});
		destroyedObjects.forEach(sceneObject -> {
			SceneObject parent = sceneObject.getParent();

			parent.getChildren().remove(sceneObject);

			sceneObject.getChildren().forEach(child -> parent.addChild(child));
		});

		destroyedObjects.clear();
	}

	final void render() {
		renderModule.render();
	}

	final void destroy(SceneObject sceneObject) {
		destroyedObjects.add(sceneObject);
	}

	@Override
	public final void dispose() {
		logicModule.dispose();
		physicsModule.dispose();
		renderModule.dispose();
	}

	final SceneObject getRoot() {
		return root;
	}

	final RenderModule getRenderModule() {
		return renderModule;
	}

	final PhysicsModule getPhysicsModule() {
		return physicsModule;
	}

}