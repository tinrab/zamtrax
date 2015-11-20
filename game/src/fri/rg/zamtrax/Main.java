package fri.rg.zamtrax;

import zamtrax.Game;

public class Main {

	public static void main(String[] args) {
		new Game.Starter()
				.setWindowSize(1280, 720)
				.setVSync(true)
				.addScene(new Gameplay())
				.setClassLoader(ClassLoader.getSystemClassLoader())
				.start();
	}

}
