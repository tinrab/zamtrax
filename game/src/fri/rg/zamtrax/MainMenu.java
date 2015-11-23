package fri.rg.zamtrax;

import fri.rg.zamtrax.level.Level;
import zamtrax.Game;
import zamtrax.Scene;

public final class MainMenu extends Scene {

	@Override
	public void onEnter() {
		Game.getInstance().enterScene(Level.class);
	}

}
