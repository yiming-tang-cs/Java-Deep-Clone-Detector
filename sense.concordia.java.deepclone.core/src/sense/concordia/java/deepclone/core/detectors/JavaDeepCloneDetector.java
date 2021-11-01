package sense.concordia.java.deepclone.core.detectors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import java.util.List;

@SuppressWarnings("unchecked")
public class JavaDeepCloneDetector extends ASTVisitor {

	private HashSet<JavaDeepCloneResult> results = new HashSet<>();

	public void detect() {

		/// TODO: add CSV printer.
		results.forEach(r -> {
			System.out.println(r.getSubject() + ": " + r.getType() + ", " + r.getFile() + ", " + r.getLine());
			System.out.println(r.getEnclosingMethod());
			System.out.println();
		});
	}

	@Override
	public boolean visit(MethodInvocation method) {

		// Check if the code invokes "clone" method
		if (isCloneMethod(method))
			this.addResult(method, JavaDeepCloneType.CLONE_METHOD);

		return super.visit(method);
	}

	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation) {
		// Check if the code clones constructor
		if (isCloneConstructor(classInstanceCreation))
			this.addResult(classInstanceCreation, JavaDeepCloneType.ClONE_CONSTRUCTOR);

		return super.visit(classInstanceCreation);
	}

	/**
	 * Check if this class instance creation is deep clone instance
	 * 
	 * @param classInstanceCreation
	 * @return True/False
	 */
	private boolean isCloneConstructor(ClassInstanceCreation classInstanceCreation) {
		String binaryName = classInstanceCreation.resolveTypeBinding().getBinaryName();
		if (binaryName != null) {
			List<Expression> args = classInstanceCreation.arguments();
			for (Expression arg : args)
				if (binaryName.equals(arg.resolveTypeBinding().getBinaryName()))
					return true;
		}
		return false;
	}

	/**
	 * Check if the method "clone" is invoked.
	 * 
	 * @param method
	 * @return True/False
	 */
	private boolean isCloneMethod(MethodInvocation method) {
		if (method.getName().toString().equals("clone")) {
			IMethodBinding methodBinding = method.resolveMethodBinding();
			if (methodBinding != null) {
				ITypeBinding[] interfraces = methodBinding.getDeclaringClass().getInterfaces();
				for (ITypeBinding inter : interfraces)
					if (inter.getBinaryName().equals("java.lang.Cloneable"))
						return true;
			}
		}

		return false;
	}

	public HashSet<JavaDeepCloneResult> getResults() {
		return this.results;
	}

	public void setResults(HashSet<JavaDeepCloneResult> results) {
		this.results = results;
	}

	private void addResult(ASTNode method, JavaDeepCloneType type) {
		JavaDeepCloneResult newResult = new JavaDeepCloneResult(method, type);
		results.add(newResult);
	}

}
