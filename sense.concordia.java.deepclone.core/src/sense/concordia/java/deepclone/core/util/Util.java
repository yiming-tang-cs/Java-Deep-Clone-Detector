package sense.concordia.java.deepclone.core.util;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
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

	/**
	 * Get the full qualified name of the method.
	 * 
	 * @param projectName
	 * @param method
	 * @return method names.
	 */
	@SuppressWarnings("unchecked")
	public static String[] getMethodFQN(MethodDeclaration method) {
		String parameterSig = getParamemterSig(method.parameters());

		System.out.println("methodDec: " + getMethodPrefix((CompilationUnit) method.getRoot()) + method.getName() + "( "
				+ parameterSig + ")");

		// Store 2 kinds of method names;
		// The first one is from Java model, the second one is from AST.
		String[] methodNames = new String[2];

		IMethodBinding methodbinding = method.resolveBinding();
		methodNames[0] = getMethodIdentifier(methodbinding);
		methodNames[1] = getMethodPrefix((CompilationUnit) method.getRoot()) + method.getName() + "( " + parameterSig
				+ ")";
		return methodNames;
	}

	private static String getMethodPrefix(CompilationUnit cu) {
		if (cu.getPackage() != null) {
			String pkgName = cu.getPackage().getName().toString();
			return pkgName + "." + cu.getJavaElement().getElementName() + "~";
		}
		return "";
	}

	public static String getParamemterSig(List<SingleVariableDeclaration> parameters) {
		String parameterSig = "";
		for (SingleVariableDeclaration p : parameters) {
			parameterSig += p.getType().toString() + ", ";
		}
		return parameterSig;
	}

	/**
	 * Get the full qualified name of the method.
	 * 
	 * @param projectName
	 * @param method
	 * @return method name.
	 */
	public static String getMethodFQN(ClassInstanceCreation method) {
		IMethodBinding methodbinding = method.resolveConstructorBinding();
		return getMethodIdentifier(methodbinding);
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
