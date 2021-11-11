package sense.concordia.java.deepclone.core.util;

import java.io.IOException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneProcessor;

@SuppressWarnings("restriction")
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

	public static String getEnclosingMethod(ASTNode method) {
		MethodDeclaration methodDec = (MethodDeclaration) ASTNodes.getParent(method, ASTNode.METHOD_DECLARATION);
		if (methodDec != null)
			return methodDec.toString();
		else
			return "";
	}

	public static String getMethodIdentifier(IMethodBinding methodbinding) {
		if (methodbinding == null)
			return "";
		else {
			IMethod methodElement = (IMethod) methodbinding.getJavaElement();
			if (methodElement == null)
				return "";
			return methodElement.getHandleIdentifier();
		}
	}

}
