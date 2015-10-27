package zamtrax.resources;

class AttributePointer {

	private AttributeType attributeType;
	private int location;
	private CharSequence name;

	public AttributePointer(AttributeType attributeType, int location, CharSequence name) {
		this.attributeType = attributeType;
		this.location = location;
		this.name = name;
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public int getLocation() {
		return location;
	}

	public CharSequence getName() {
		return name;
	}

}
