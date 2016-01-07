package zamtrax.ui;

import zamtrax.Component;
import zamtrax.Matrix4;

public class Canvas extends Component {

	private SpriteBatch spriteBatch;

	@Override
	public void onAdd() {
		spriteBatch = new SpriteBatch();
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public void setProjection(Matrix4 projection) {
		spriteBatch.setProjection(projection);
	}

	public Matrix4 getProjection(){
		return spriteBatch.getProjection();
	}

}
