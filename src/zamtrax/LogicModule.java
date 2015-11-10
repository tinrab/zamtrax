package zamtrax;

import java.util.List;

class LogicModule extends Module {

	LogicModule(Scene scene) {
		super(scene);
	}

	@Override
	public void update() {
		update(scene.getRoot());
	}

	private void update(SceneObject sceneObject) {
		sceneObject.getComponents().forEach(SceneComponent::update);

		sceneObject.getTransform().update();

		//sceneObject.getChildren().forEach(this::update);
		List<SceneObject> children = sceneObject.getChildren();

		for (int i = 0; i < children.size(); i++) {
			SceneObject child = children.get(i);

			update(child);
		}
	}

	@Override
	public void dispose() {
	}

}
