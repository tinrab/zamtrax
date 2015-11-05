package zamtrax;

public class LogicModule extends Module {

	protected LogicModule(Scene scene) {
		super(scene);
	}

	@Override
	public void update() {
		update(scene.getRoot());
	}

	private void update(SceneObject sceneObject) {
		sceneObject.getComponents().forEach(SceneComponent::update);

		sceneObject.getTransform().update();
		sceneObject.getChildren().forEach(this::update);
	}

	@Override
	public void dispose() {
	}

}
