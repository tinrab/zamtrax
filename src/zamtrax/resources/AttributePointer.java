package zamtrax.resources;

class AttributePointer {

	private AttributeType attributeType;
	private int location;

	public AttributePointer(AttributeType attributeType, int location) {
		this.attributeType = attributeType;
		this.location = location;
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public int getLocation() {
		return location;
	}

}
