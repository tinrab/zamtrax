package zamtrax.resources;

class Uniform {

	private CharSequence name;
	private int location;

	public Uniform(CharSequence name) {
		this.name = name;
	}

	public CharSequence getName() {
		return name;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

}
