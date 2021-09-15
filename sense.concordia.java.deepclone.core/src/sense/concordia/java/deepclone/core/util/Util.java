package sense.concordia.java.deepclone.core.util;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneProcessor;

public final class Util {
	
	/**
	 * Create a processor for Java deep clone detection.
	 * @param projects: the selected projects.
	 * @return processor
	 * @throws JavaModelException
	 */
	public static JavaDeepCloneProcessor createDeepCloneProcessor(IJavaProject[] projects) throws JavaModelException {
		if (projects.length < 1)
			throw new IllegalArgumentException("No selected projects.");
		
		return new JavaDeepCloneProcessor(projects);
		
	}


	/**
	 * Let the processor detect the java deep clone.
	 * @param proejcts
	 * @throws JavaModelException
	 */
	public static void deepclonedetect(IJavaProject[] proejcts) throws JavaModelException {
		JavaDeepCloneProcessor processor = Util.createDeepCloneProcessor(proejcts);
		processor.process();
		
	}

}
