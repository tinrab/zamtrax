package zamtrax.ui;

import zamtrax.Component;

public class Canvas extends Component {

	private SpriteBatch spriteBatch;

	@Override
	public void onAdd() {
		spriteBatch = new SpriteBatch();
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

}
