package sense.concordia.java.deepclone.core.util;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneProcessor;

public final class Util {
	
	public static JavaDeepCloneProcessor createDeepCloneProcessor(IJavaProject[] projects) throws JavaModelException {
		if (projects.length < 1)
			throw new IllegalArgumentException("No selected projects.");

		JavaDeepCloneProcessor processor = new JavaDeepCloneProcessor(projects);
		return processor;
	}

}
