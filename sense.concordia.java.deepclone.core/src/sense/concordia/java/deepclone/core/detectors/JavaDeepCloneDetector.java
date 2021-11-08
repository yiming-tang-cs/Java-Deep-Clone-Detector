package sense.concordia.java.deepclone.core.detectors;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import sense.concordia.java.deepclone.core.util.Util;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;

@SuppressWarnings("unchecked")
public class JavaDeepCloneDetector extends ASTVisitor {

	private HashSet<JavaDeepCloneResult> results = new HashSet<>();

	// Store all related method declarations from the first scanning
	private HashSet<String> constructors = new HashSet<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();
	private HashSet<String> cloneableMethods = new HashSet<>();

	// Store all related method declarations from the first scanning
	private HashSet<String> constructorsAST = new HashSet<>();
	private HashSet<String> serializableMethodNamesAST = new HashSet<>();
	private HashSet<String> cloneableMethodsAST = new HashSet<>();

	// Store the importing packages to get the fully qualified names for the methods
	// declared in them
	private HashSet<String> imports = new HashSet<>();
	private HashMap<String, String> typeToClass = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param serializableMethodNames: the method declaration for serialization.
	 * @param cloneableMethods:        the method declaration to override
	 *                                 Object.clone().
	 */
	public JavaDeepCloneDetector(HashSet<String> serializableMethodNames, HashSet<String> cloneableMethods,
			HashSet<String> constructors, HashSet<String> serializableMethodNamesAST,
			HashSet<String> cloneableMethodsAST, HashSet<String> constructorsAST) {
		this.serializableMethodNames = serializableMethodNames;
		this.cloneableMethods = cloneableMethods;
		this.constructors = constructors;

		this.setSerializableMethodNamesAST(serializableMethodNamesAST);
		this.setCloneableMethodsAST(cloneableMethodsAST);
		this.setConstructorsAST(constructorsAST);
	}

	@Override
	public boolean visit(ImportDeclaration importDeclaration) {
		System.out.println(importDeclaration.getName().getFullyQualifiedName());
		this.imports.add(importDeclaration.getName().getFullyQualifiedName());
		return super.visit(importDeclaration);
	}

	@Override
	public boolean visit(VariableDeclarationFragment variableDeclarationFragment) {

		String type = "";

		ASTNode parent = variableDeclarationFragment.getParent();

		if (parent instanceof VariableDeclarationStatement)
			type = ((VariableDeclarationStatement) parent).getType().toString();

		if (parent instanceof FieldDeclaration)
			type = ((FieldDeclaration) parent).getType().toString();

		System.out.println("type???: " + type + "    ////" + variableDeclarationFragment.getName().toString());

		this.typeToClass.put(type, variableDeclarationFragment.getName().toString());
		return super.visit(variableDeclarationFragment);
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
		String[] methodNames = this.getMethodFQN(method);
		if (methodNames[0].isBlank()) { // Check names from AST
			if (this.serializableMethodNamesAST.contains(methodNames[1]))
				return true;
		} else { // Check names from Java model
			if (this.serializableMethodNames.contains(methodNames[0]))
				return true;
		}
		return false;
	}

	/**
	 * Check if this class instance creation is deep clone instance
	 * 
	 * @param classInstanceCreation
	 * @return True/False
	 */
	private boolean isCloneConstructor(ClassInstanceCreation classInstanceCreation) {
		String constructorName = this.getMethodFQN(classInstanceCreation);
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
		String[] targetMethodNames = this.getMethodFQN(method);
		if (this.cloneableMethods.containsKey(targetMethodName)) {
			return true;
		}
		return false;
	}

	/**
	 * Get the full qualified name of the method.
	 * 
	 * @param projectName
	 * @param method
	 * @return method name.
	 */
	public String[] getMethodFQN(MethodInvocation method) {
		String[] methodNames = new String[2];

		IMethodBinding methodbinding = method.resolveMethodBinding();
		String methodname = Util.getMethodIdentifier(methodbinding);
		if (methodname.isBlank()) {
			methodNames[0] = "";
			methodNames[1] = getFQNforMethod(method.getExpression()) + ".java~" + method.getName() + "("
					+ Util.getParamemterSig(method.arguments()) + ")";//////////////// check

			System.out.println(methodNames[1] + "**********");
		} else {
			methodNames[0] = methodname;
			methodNames[1] = "";
		}
		return methodNames;
	}

	private String getFQNforMethod(Expression methodExpression) {
		String importString = "";
		if (this.typeToClass.containsKey(methodExpression.toString())) {
			String classname = this.typeToClass.get(methodExpression.toString());
			for (String im : this.imports) {
				if (im.contains(classname)) {
					importString = im;
					break;
				}
			}
		}
		return importString;
	}

	/**
	 * Get the full qualified name of the method.
	 * 
	 * @param projectName
	 * @param method
	 * @return method name.
	 */
	public String[] getMethodFQN(ClassInstanceCreation method) {
		String[] methodNames = new String[2];

		IMethodBinding methodbinding = method.resolveConstructorBinding();
		String methodname = Util.getMethodIdentifier(methodbinding);
		if (methodname.isBlank()) {
			methodNames[0] = "";
			methodNames[1] = getFQNforMethod(method.getExpression()) + ".java~" + method.getType().toString() + "("
					+ Util.getParamemterSig(method.arguments()) + ")";//////////////// check

			System.out.println(methodNames[1] + "**********");
		} else {
			methodNames[0] = methodname;
			methodNames[1] = "";
		}
		return methodNames;
	}

	private static String getMethodPrefix(CompilationUnit root) {
		// TODO Auto-generated method stub
		return "";
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

	public HashSet<String> getConstructorsAST() {
		return constructorsAST;
	}

	public void setConstructorsAST(HashSet<String> constructorsAST) {
		this.constructorsAST = constructorsAST;
	}

	public HashSet<String> getSerializableMethodNamesAST() {
		return serializableMethodNamesAST;
	}

	public void setSerializableMethodNamesAST(HashSet<String> serializableMethodNamesAST) {
		this.serializableMethodNamesAST = serializableMethodNamesAST;
	}

	public HashSet<String> getCloneableMethodsAST() {
		return cloneableMethodsAST;
	}

	public void setCloneableMethodsAST(HashSet<String> cloneableMethodsAST) {
		this.cloneableMethodsAST = cloneableMethodsAST;
	}

}
