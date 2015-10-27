package zamtrax.resources;

public enum AttributeType {
	POSITION(3), COLOR(4), NORMAL(3), UV(2);

	private final int size;

	AttributeType(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

}