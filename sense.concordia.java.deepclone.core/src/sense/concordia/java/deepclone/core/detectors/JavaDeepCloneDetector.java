package sense.concordia.java.deepclone.core.detectors;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import sense.concordia.java.deepclone.core.util.Util;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

@SuppressWarnings("unchecked")
public class JavaDeepCloneDetector extends ASTVisitor {

	private String projectName;

	private HashSet<JavaDeepCloneResult> results = new HashSet<>();

	// Store all related method declarations from the first scanning
	private HashSet<String> constructors = new HashSet<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();
	private HashMap<String, MethodDeclaration> cloneableMethods = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param serializableMethodNames: the method declaration for serialization.
	 * @param cloneableMethods:        the method declaration to override
	 *                                 Object.clone().
	 */
	public JavaDeepCloneDetector(HashSet<String> serializableMethodNames,
			HashMap<String, MethodDeclaration> cloneableMethods, HashSet<String> constructors, String projectName) {
		this.serializableMethodNames = serializableMethodNames;
		this.cloneableMethods = cloneableMethods;
		this.constructors = constructors;
		this.setProjectName(projectName);
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
		if (this.serializableMethodNames.contains(Util.getMethodFQN(this.projectName, method)))
			return true;
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
		String constructorName = Util.getMethodFQN(projectName, classInstanceCreation);
		if (this.constructors.contains(constructorName)) {

			ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
			if (typeBinding != null) {

				String binaryName = typeBinding.getBinaryName();

				List<Expression> args = classInstanceCreation.arguments();
				for (Expression arg : args)
					if (binaryName.equals(arg.resolveTypeBinding().getBinaryName()))
						return true;
			}

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

		IMethodBinding methodBinding = method.resolveMethodBinding();
		if (methodBinding != null) {
			ITypeBinding[] interfraces = methodBinding.getDeclaringClass().getInterfaces();
			if (this.checkInterfaces(interfraces, "java.lang.Cloneable"))
				return this.checkContentInCloneMethod(method);
		}
		return false;
	}

	/**
	 * Check if clone method uses deep clone. This check is very rough and may be
	 * improved later.
	 * 
	 * @param clone method
	 * @return True/False
	 */
	private boolean checkContentInCloneMethod(MethodInvocation method) {
		String targetMethodName = Util.getMethodFQN(projectName, method);
		if (this.cloneableMethods.containsKey(targetMethodName)) {
			MethodDeclaration methodDeclaration = cloneableMethods.get(targetMethodName);
			// Except for super.clone(), there should be at least one more statement
			// TODO: enhance detection algorithm here.
			if (methodDeclaration.getBody().statements().size() > 1)
				return true;
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

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
