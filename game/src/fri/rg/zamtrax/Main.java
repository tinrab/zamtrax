package fri.rg.zamtrax;

import zamtrax.Game;

public class Main {

	public static void main(String[] args) {
		new Game.Starter()
				.setWindowSize(1280, 720)
				.addScene(new Gameplay())
				.setClassLoader(ClassLoader.getSystemClassLoader())
				.start();
	}

}
