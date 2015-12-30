package fri.rg.zamtrax.level.player;

import fri.rg.zamtrax.level.Level;
import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.CharacterController;

public class Player extends Component {

	@Override
	public void onAdd() {
		getTransform().setPosition(Level.getInstance().getArena().getSpawnPoint());

		Camera camera = GameObject.create(getGameObject()).addComponent(Camera.class);
		Camera.setMainCamera(camera);

		camera.setProjection(Matrix4.createPerspective(60.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 500.0f));

		getGameObject().addComponent(CharacterController.class);
		getGameObject().addComponent(PlayerController.class);
	}

}
