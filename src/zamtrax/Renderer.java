package zamtrax;

public abstract class Renderer extends SceneComponent {

	public abstract void render(Matrix4 viewProjection);

	@Override
	public void onAdd() {
		Game.getInstance().getCurrentScene().getRenderModule().addRenderer(this);
	}

	@Override
	public void onRemove() {
		Game.getInstance().getCurrentScene().getRenderModule().removeRenderer(this);
	}

}
