package zamtrax.resources;

public class Uniform {

	private String name;
	private int location;

	public Uniform(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

}
