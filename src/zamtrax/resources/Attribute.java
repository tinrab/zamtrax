package zamtrax.resources;

final class Attribute {

	private CharSequence name;
	private int location;

	public Attribute(CharSequence name, int location) {
		this.name = name;
		this.location = location;
	}

	public CharSequence getName() {
		return name;
	}

	public int getLocation() {
		return location;
	}

}
