package fri.rg.zamtrax;

import fri.rg.zamtrax.menu.MainMenu;
import zamtrax.Game;

public class Main {

	public static void main(String[] args) {
		new Game.Starter()
				.setWindowSize(1600, 900)
				.setVSync(true)
				.setStartScene(MainMenu.class)
				.setClassLoader(ClassLoader.getSystemClassLoader())
				.start();
	}

}
