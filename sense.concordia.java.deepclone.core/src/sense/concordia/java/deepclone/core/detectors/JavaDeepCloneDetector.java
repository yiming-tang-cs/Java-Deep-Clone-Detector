package sense.concordia.java.deepclone.core.detectors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;

@SuppressWarnings("unchecked")
public class JavaDeepCloneDetector extends ASTVisitor {

	private HashSet<JavaDeepCloneResult> results = new HashSet<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();

	public void detect() {

		/// TODO: add CSV printer.
		results.forEach(r -> {
			System.out.println(r.getSubject() + ": " + r.getType() + ", " + r.getFile() + ", " + r.getLine());
			System.out.println(r.getEnclosingMethod());
			System.out.println();
		});
	}

	@Override
	public boolean visit(MethodDeclaration method) {
		if (isSerializationMethodDec(method))
			this.serializableMethodNames.add(method.getName().getFullyQualifiedName());
		return super.visit(method);
	}

	private boolean isSerializationMethodDec(MethodDeclaration method) {
		String methodBody = method.getBody().toString();
		if (methodBody.contains("ObjectOutputStream") && methodBody.contains("ObjectInputStream"))
			return true;
		else
			return false;
	}

	@Override
	public boolean visit(MethodInvocation method) {
		// Check if the code invokes "clone" method
		if (isCloneMethod(method)) // Detect cloneable interface
			this.addResult(method, JavaDeepCloneType.CLONE_METHOD);
		else if (isSerialization(method)) // Detect java serialization
			this.addResult(method, JavaDeepCloneType.CLONE_SERIALIZATION);

		return super.visit(method);
	}

	/**
	 * Check if this method is used to serialize the object into bytes and from
	 * bytes to object again.
	 * 
	 * @param method
	 * @return True/False
	 */
	private boolean isSerialization(MethodInvocation method) {
		if (this.serializableMethodNames.contains(method.getName().getFullyQualifiedName()))
			return true;

		ITypeBinding typeBinding = method.resolveTypeBinding();
		if (typeBinding != null) {
			// Check if the class implements Serializable interface
			ITypeBinding[] interfaces = typeBinding.getInterfaces();
			if (!this.checkInterfaces(interfaces, "java.io.Serializable"))
				return false;
		}
		return false;
	}

	/**
	 * Check if an interface is in the interface array.
	 * 
	 * @param interfaces
	 * @param interString
	 * @return True/False
	 */
	private boolean checkInterfaces(ITypeBinding[] interfaces, String interString) {
		for (ITypeBinding inter : interfaces) {
			if (inter.getBinaryName().equals(interString))
				return true;
		}
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation) {
		// Check if the code clones constructor
		if (isCloneConstructor(classInstanceCreation))
			this.addResult(classInstanceCreation, JavaDeepCloneType.CLONE_CONSTRUCTOR);

		return super.visit(classInstanceCreation);
	}

	/**
	 * Check if this class instance creation is deep clone instance
	 * 
	 * @param classInstanceCreation
	 * @return True/False
	 */
	private boolean isCloneConstructor(ClassInstanceCreation classInstanceCreation) {
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		if (typeBinding != null) {
			String binaryName = typeBinding.getBinaryName();

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
				return this.checkInterfaces(interfraces, "java.lang.Cloneable");
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

	public HashSet<String> getSerializationMethodDec() {
		return serializableMethodNames;
	}

	public void setSerializationMethodDec(HashSet<String> serializableMethodNames) {
		this.serializableMethodNames = serializableMethodNames;
	}

}
