package de.cyface.smoothing;

public enum Filter {
	RECTANGULAR("Rectangular");
	
	private final String name;
	
	private Filter(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
