package zamtrax.resources;

import java.util.ArrayList;
import java.util.List;

public interface BindingInfo {

	List<AttributePointer> getAttributePointers();

	int getSize();

	class Builder {

		private class BindingInfoImpl implements BindingInfo {

			private List<AttributePointer> attributePointers;
			private int size;

			BindingInfoImpl(List<AttributePointer> attributePointers) {
				this.attributePointers = attributePointers;

				attributePointers.forEach(ap -> size += ap.getAttributeType().getSize());
			}

			@Override
			public List<AttributePointer> getAttributePointers() {
				return attributePointers;
			}

			@Override
			public int getSize() {
				return size;
			}

		}

		private List<Attribute> attributes;
		private List<AttributePointer> attributePointers;

		public Builder() {
			attributePointers = new ArrayList<>();
		}

		public Builder bind(AttributeType attributeType, int location, CharSequence name) {
			attributePointers.add(new AttributePointer(attributeType, location, name));

			return this;
		}

		public BindingInfo build() {
			return new BindingInfoImpl(attributePointers);
		}

	}

}
