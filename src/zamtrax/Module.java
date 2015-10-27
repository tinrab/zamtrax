package zamtrax;

public abstract class Module implements Disposable {

	protected Scene scene;

	protected Module(Scene scene) {
		this.scene = scene;
	}

	public void onCreate() {
	}

	public void update() {
	}

	public void render() {
	}

}
