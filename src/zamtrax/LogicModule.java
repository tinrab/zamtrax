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

	private void update(GameObject sceneObject) {
		sceneObject.getComponents().forEach(Component::update);

		sceneObject.getTransform().update();

		//sceneObject.getChildren().forEach(this::update);
		List<GameObject> children = sceneObject.getChildren();

		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);

			update(child);
		}
	}

	@Override
	public void dispose() {
	}

}
