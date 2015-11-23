package fri.rg.zamtrax;

import fri.rg.zamtrax.level.Level;
import zamtrax.Game;

public class Main {

	public static void main(String[] args) {
		new Game.Starter()
				.setWindowSize(1280, 720)
				.setVSync(true)
				.addScene(new Level())
				.setClassLoader(ClassLoader.getSystemClassLoader())
				.start();
	}

}
