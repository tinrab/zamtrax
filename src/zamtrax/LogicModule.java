package zamtrax;

import java.util.List;

class LogicModule extends Module {

	LogicModule(Scene scene) {
		super(scene);
	}

	@Override
	public void update(float delta) {
		update(delta, scene.getRoot());
	}

	private void update(float delta, GameObject sceneObject) {
		sceneObject.getComponents().forEach(component -> {
			if (component.isEnabled()) {
				component.update(delta);
			}
		});

		//sceneObject.getChildren().forEach(this::update);
		List<GameObject> children = sceneObject.getChildren();

		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);

			if(child.isActive()) {
				update(delta, child);
			}
		}
	}

	@Override
	public void dispose() {
	}

}
