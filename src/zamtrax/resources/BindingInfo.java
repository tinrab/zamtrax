package zamtrax.resources;

import java.util.ArrayList;
import java.util.List;

public class BindingInfo {

	private List<AttributePointer> attributePointers;
	private int size;

	public BindingInfo(AttributeType... attributeTypes) {
		attributePointers = new ArrayList<>();

		for (AttributeType type : attributeTypes) {
			attributePointers.add(new AttributePointer(type, attributePointers.size()));
			size += type.getSize();
		}
	}

	public BindingInfo(List<AttributePointer> attributePointers) {
		this.attributePointers = attributePointers;

		attributePointers.forEach(ap -> size += ap.getAttributeType().getSize());
	}

	public List<AttributePointer> getAttributePointers() {
		return attributePointers;
	}

	public int getSize() {
		return size;
	}

}
