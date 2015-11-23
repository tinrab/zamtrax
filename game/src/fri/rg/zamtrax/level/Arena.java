package fri.rg.zamtrax.level;

import zamtrax.Vector3;

public class Arena {

	public static final int SIZE = 32;
	public static final int HEIGHT = 16;

	private Vector3 spawnPoint;

	public Arena() {
		spawnPoint = new Vector3(0.0f, 5.0f, 0.0f);
	}

	public Vector3 getSpawnPoint() {
		return spawnPoint;
	}

}
