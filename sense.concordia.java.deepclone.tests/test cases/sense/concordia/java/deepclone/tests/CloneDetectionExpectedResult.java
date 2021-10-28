package sense.concordia.java.deepclone.tests;

import java.util.EnumSet;
import java.util.Set;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneType;

public class CloneDetectionExpectedResult {
	private EnumSet<JavaDeepCloneType> types;
	private Set<String> cloneLocations;

	public CloneDetectionExpectedResult(EnumSet<JavaDeepCloneType> types, Set<String> cloneLocations) {
		this.setTypes(types);
		this.setCloneLocations(cloneLocations);
	}

	public EnumSet<JavaDeepCloneType> getTypes() {
		return this.types;
	}

	public void setTypes(EnumSet<JavaDeepCloneType> types) {
		this.types = types;
	}

	public Set<String> getCloneLocations() {
		return this.cloneLocations;
	}

	public void setCloneLocations(Set<String> cloneLocations) {
		this.cloneLocations = cloneLocations;
	}

}
