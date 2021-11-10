package sense.concordia.java.deepclone.core.detectors;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import sense.concordia.java.deepclone.core.util.Util;

public class JavaMethodDeclarationDetector extends ASTVisitor {

	private HashSet<String> cloneableMethods = new HashSet<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();
	private HashSet<String> constructors = new HashSet<String>();

	// These sets are used to store fully qualified names for interprocedural
	// analysis
	private HashSet<String> cloneableMethodsAST = new HashSet<>();
	private HashSet<String> serializableMethodNamesAST = new HashSet<>();
	private HashSet<String> constructorsAST = new HashSet<String>();

	@Override
	public boolean visit(MethodDeclaration method) {
		if (isSerializationMethodDec(method)) {
			String[] methodNames = Util.getMethodFQN(method);
			this.serializableMethodNames.add(methodNames[0]);
			this.serializableMethodNamesAST.add(methodNames[1]);
		} else if (isCloneableMethod(method)) {
			String[] methodNames = Util.getMethodFQN(method);
			this.cloneableMethods.add(methodNames[0]);
			this.cloneableMethodsAST.add(methodNames[1]);
		} else if (isValidConstructorMethod(method)) {
			String[] methodNames = Util.getMethodFQN(method);
			this.constructors.add(methodNames[0]);
			this.constructorsAST.add(methodNames[1]);
		}

		return super.visit(method);
	}

	private boolean isSerializationMethodDec(MethodDeclaration method) {
		Block methodBodyBlock = method.getBody();
		if (methodBodyBlock != null) {
			String methodBody = methodBodyBlock.toString();
			if (methodBody.contains("ObjectOutputStream") && methodBody.contains("ObjectInputStream")
					&& methodBody.contains("ByteArrayOutputStream") && methodBody.contains("ByteArrayInputStream"))
				return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean isValidConstructorMethod(MethodDeclaration method) {

		if (method.isConstructor()) {
			ITypeBinding typeBinding = method.resolveBinding().getDeclaringClass();
			if (typeBinding != null) {

				String binaryName = typeBinding.getBinaryName();
				System.out.println("Constructor: " + binaryName);
				List<SingleVariableDeclaration> args = method.parameters();
				for (SingleVariableDeclaration arg : args) {
					ITypeBinding declaringclass = arg.resolveBinding().getDeclaringClass();
					if (declaringclass != null) {
						System.out.println("Constructor arg: " + declaringclass.getBinaryName());
						if (binaryName.equals(declaringclass.getBinaryName()))
							return true;	
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check if the method name is "clone" or not.
	 * 
	 * This method should be an overriding method.
	 * 
	 * @param methodName
	 * @return True/False
	 */
	private boolean isCloneableMethod(MethodDeclaration method) {
		if (method.getName().toString().equals("clone")) {

			// Except for super.clone(), there should be at least one more statement
			// TODO: enhance detection algorithm here.
			if (method.getBody().statements().size() <= 1)
				return false;

			IMethodBinding methodBinding = method.resolveBinding();
			if (methodBinding != null) {
				IMethodBinding[] superMethods = methodBinding.getDeclaringClass().getSuperclass().getDeclaredMethods();
				for (IMethodBinding superMethod : superMethods) {
					if (methodBinding.getMethodDeclaration().overrides(superMethod))
						return true;
				}
			}
		}
		return false;
	}

	public HashSet<String> getSerializableMethodNames() {
		return this.serializableMethodNames;
	}

	public HashSet<String> getSerializableMethodNamesAST() {
		return this.serializableMethodNamesAST;
	}

	public HashSet<String> getCloneableMethods() {
		return this.cloneableMethods;
	}
	
	public HashSet<String> getCloneableMethodsAST() {
		return this.cloneableMethodsAST;
	}

	public HashSet<String> getConstructors() {
		return this.constructors;
	}
	
	public HashSet<String> getConstructorsAST() {
		return this.constructorsAST;
	}

	public void setConstructors(HashSet<String> constructors) {
		this.constructors = constructors;
	}

}
