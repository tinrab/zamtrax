package zamtrax.resources;

import zamtrax.Disposable;

abstract class ReferencedResource implements Disposable {

	private int count;

	public ReferencedResource() {
		count = 1;
	}

	public void addReference() {
		count++;
	}

	public boolean removeReference() {
		count--;

		if (count == 0) {
			dispose();

			return true;
		}

		return false;
	}

	public int getReferenceCount() {
		return count;
	}

}
