package zamtrax.resources;

public enum AttributeType {
	POSITION(4, "POSITION"), COLOR(4, "COLOR"), NORMAL(3, "NORMAL"), UV(2, "UV");

	private final int size;
	private final String name;

	AttributeType(int size, String name) {
		this.size = size;
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

}
