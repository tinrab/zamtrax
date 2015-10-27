package zamtrax;

public class Scene implements Disposable {

	private SceneObject root;

	private PhysicsModule physicsModule;
	private RenderModule renderModule;

	protected Scene() {
		root = new SceneObject();

		physicsModule = new PhysicsModule(this);
		renderModule = new RenderModule(this);
	}

	public void onEnter() {
		physicsModule.onCreate();
		renderModule.onCreate();
	}

	public void onExit() {
	}

	final void update() {
		update(root);

		physicsModule.update();
		renderModule.update();
	}

	final void update(SceneObject sceneObject) {
		sceneObject.getComponents().forEach(SceneComponent::update);

		//sceneObject.getTransform().update();
		sceneObject.getChildren().forEach(this::update);
	}

	final void render() {
		renderModule.render();
	}

	@Override
	public final void dispose() {
		physicsModule.dispose();
		renderModule.dispose();
	}

	final SceneObject getRoot() {
		return root;
	}

}
