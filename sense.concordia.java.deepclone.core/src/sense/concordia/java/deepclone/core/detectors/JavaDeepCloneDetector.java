package sense.concordia.java.deepclone.core.detectors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import sense.concordia.java.deepclone.core.util.Util;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;

public class JavaDeepCloneDetector extends ASTVisitor {

	private HashSet<JavaDeepCloneResult> results = new HashSet<>();

	// Store all related method declarations from the first scanning
	private HashSet<String> constructors = new HashSet<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();
	private HashSet<String> cloneableMethods = new HashSet<>();

	/**
	 * Constructor.
	 * 
	 * @param serializableMethodNames: the method declaration for serialization.
	 * @param cloneableMethods:        the method declaration to override
	 *                                 Object.clone().
	 */
	public JavaDeepCloneDetector(HashSet<String> serializableMethodNames, HashSet<String> cloneableMethods,
			HashSet<String> constructors) {
		this.serializableMethodNames = serializableMethodNames;
		this.cloneableMethods = cloneableMethods;
		this.constructors = constructors;

	}

	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation) {
		// Check if the code clones constructor
		if (isCloneConstructor(classInstanceCreation))
			this.addResult(classInstanceCreation, JavaDeepCloneType.CLONE_CONSTRUCTOR);

		return super.visit(classInstanceCreation);
	}

	@Override
	public boolean visit(MethodInvocation method) {

		String methodName = method.getName().toString();

		if (methodName.equals("clone")) {
			// Check if the code invokes "clone" method
			if (isCloneMethod(method)) {// Detect cloneable interface
				this.addResult(method, JavaDeepCloneType.CLONE_METHOD);
				return super.visit(method);
			}
			if (isApacheCommonsClone(method)) {
				this.addResult(method, JavaDeepCloneType.CLONE_APACHE_COMMONS);
				return super.visit(method);
			}
		}

		String enclosingMethod = Util.getEnclosingMethod(method);

		if (!enclosingMethod.isBlank()) {
			if (methodName.equals("fromJson")) {
				if (isGsonCloneMethod(method, enclosingMethod)) {
					this.addResult(method, JavaDeepCloneType.CLONE_GSON);
					return super.visit(method);
				}
			}

			if (methodName.equals("readValue")) {
				if (isJacksonCloneMethod(method, enclosingMethod)) {
					this.addResult(method, JavaDeepCloneType.CLONE_JACKSON);
					return super.visit(method);
				}
			}
		}

		if (isSerialization(method)) // Detect java serialization
			this.addResult(method, JavaDeepCloneType.CLONE_SERIALIZATION);

		return super.visit(method);
	}

	/**
	 * Check if the Jackson deep clone method is invoked.
	 * 
	 * @param method
	 * @param enclosingMethod
	 * @return True/False
	 */
	private boolean isJacksonCloneMethod(MethodInvocation method, String enclosingMethod) {
		IMethodBinding methodBinding = method.resolveMethodBinding();
		if (methodBinding != null
				&& methodBinding.getDeclaringClass().getQualifiedName()
						.equals("com.fasterxml.jackson.databind.ObjectMapper")
				&& enclosingMethod.contains("writeValueAsString"))
			return true;
		return false;
	}

	/**
	 * Check if the Gson deep clone method is invoked.
	 * 
	 * @param method
	 * @param enclosingMethod
	 * @return True/False
	 */
	private boolean isGsonCloneMethod(MethodInvocation method, String enclosingMethod) {
		IMethodBinding methodBinding = method.resolveMethodBinding();
		if (methodBinding != null && methodBinding.getDeclaringClass().getQualifiedName().equals("com.google.gson.Gson")
				&& enclosingMethod.contains("toJson"))
			return true;
		return false;
	}

	/**
	 * Check if the Apache Commons deep clone method is invoked
	 * 
	 * @param method
	 * @return True/False
	 */
	private boolean isApacheCommonsClone(MethodInvocation method) {
		IMethodBinding methodBinding = method.resolveMethodBinding();
		if (methodBinding != null && methodBinding.getDeclaringClass().getQualifiedName()
				.equals("org.apache.commons.lang.SerializationUtils"))
			return true;

		return false;
	}

	/**
	 * Check if this method is used to serialize the object into bytes and from
	 * bytes to object again.
	 * 
	 * @param method
	 * @return True/False
	 */
	private boolean isSerialization(MethodInvocation method) {
		IMethodBinding methodbinding = method.resolveMethodBinding();
		String methodname = Util.getMethodIdentifier(methodbinding);

		// Check names from Java model
		if (this.serializableMethodNames.contains(methodname))
			return true;

		return false;
	}

	/**
	 * Check if this class instance creation is deep clone instance
	 * 
	 * @param classInstanceCreation
	 * @return True/False
	 */
	private boolean isCloneConstructor(ClassInstanceCreation classInstanceCreation) {
		IMethodBinding methodbinding = classInstanceCreation.resolveConstructorBinding();
		String methodname = Util.getMethodIdentifier(methodbinding);

		if (this.constructors.contains(methodname))
			return false;

		return false;
	}

	/**
	 * Check if the method "clone" is invoked.
	 * 
	 * @param method
	 * @return True/False
	 */
	private boolean isCloneMethod(MethodInvocation method) {
		IMethodBinding methodbinding = method.resolveMethodBinding();
		String methodname = Util.getMethodIdentifier(methodbinding);

		if (this.cloneableMethods.contains(methodname))
			return true;

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
		this.results.add(newResult);
	}

	public HashSet<String> getSerializationMethodDec() {
		return this.serializableMethodNames;
	}

	public void setSerializationMethodDec(HashSet<String> serializableMethodNames) {
		this.serializableMethodNames = serializableMethodNames;
	}

	public HashSet<String> getConstructors() {
		return constructors;
	}

	public void setConstructors(HashSet<String> constructors) {
		this.constructors = constructors;
	}

}
