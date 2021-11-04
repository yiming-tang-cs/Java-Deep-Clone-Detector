package sense.concordia.java.deepclone.core.util;

import java.io.IOException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneProcessor;

public final class Util {

	/**
	 * Create a processor for Java deep clone detection.
	 * 
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
	 * 
	 * @param proejcts
	 * @throws JavaModelException
	 * @throws IOException
	 */
	public static void deepclonedetect(IJavaProject[] proejcts) throws JavaModelException, IOException {
		JavaDeepCloneProcessor processor = Util.createDeepCloneProcessor(proejcts);
		processor.process();
	}

}
