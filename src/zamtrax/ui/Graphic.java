package zamtrax.ui;

import zamtrax.*;

public abstract class Graphic extends Component {

	protected Rectangle bounds;
	private boolean pressed, wasPressed;
	private boolean released;
	protected Canvas canvas;

	@Override
	public void onAdd() {
		super.onAdd();

		canvas = findComponentInAncestorsOfType(Canvas.class);

		if (canvas == null) {
			throw new RuntimeException(getClass().getName() + " must be in canvas");
		}
	}

	public abstract void render(SpriteBatch spriteBatch);

	@Override
	public void update(float delta) {
		if (bounds == null) {
			return;
		}

		boolean prevPressed = pressed;
		pressed = false;
		released = false;
		wasPressed = false;

		if (Input.getMouseButton(Input.MOUSE_BUTTON_1)) {
			Vector2 mousePosition = Input.getMousePosition();
			Vector3 vp = canvas.getProjection().transformPoint(new Vector3(mousePosition));
			float sw = Game.getScreenWidth() / 2.0f;
			float sh = Game.getScreenHeight() / 2.0f;
			Vector3 screenPosition = vp.mul(sw, -sh, 1.0f).add(sw, sh, 0.0f);

			if (getBounds().contains(new Vector2(screenPosition.x, screenPosition.y))) {
				pressed = true;
			}
		}

		if (prevPressed && !pressed) {
			released = true;
		}

		if (!prevPressed && pressed) {
			wasPressed = true;
		}
	}

	@Override
	public void onDisable() {
		pressed = false;
		released = false;
		wasPressed = false;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean wasReleased() {
		return released;
	}

	public boolean wasPressed() {
		return wasPressed;
	}

	public Rectangle getBounds() {
		Vector3 worldPosition = transform.getPosition();
		Vector3 scale = transform.getScale();

		return new Rectangle(bounds.x * scale.x + worldPosition.x, bounds.y * scale.y + worldPosition.y, bounds.width * scale.x, bounds.height * scale.y);
	}

}
