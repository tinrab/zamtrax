package fri.rg.zamtrax;

import fri.rg.zamtrax.menu.MainMenu;
import zamtrax.Game;
import zamtrax.Scene;

public class SplashScene extends Scene {

	@Override
	public void onEnter() {
		Game.getInstance().enterScene(MainMenu.class);
	}

}
