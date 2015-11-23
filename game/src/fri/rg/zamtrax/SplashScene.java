package fri.rg.zamtrax;

import zamtrax.Game;
import zamtrax.Scene;

public class SplashScene extends Scene {

	@Override
	public void onEnter() {
		Game.getInstance().enterScene(MainMenu.class);
	}

}
