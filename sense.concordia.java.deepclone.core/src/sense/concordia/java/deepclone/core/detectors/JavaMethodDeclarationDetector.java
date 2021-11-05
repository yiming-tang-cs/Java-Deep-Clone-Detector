package sense.concordia.java.deepclone.core.detectors;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import sense.concordia.java.deepclone.core.util.Util;

public class JavaMethodDeclarationDetector extends ASTVisitor {

	private HashMap<String, MethodDeclaration> cloneableMethods = new HashMap<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();
	private HashSet<String> constructors = new HashSet<String>();

	private String projectName;

	public JavaMethodDeclarationDetector(String projectName) {
		this.setProjectName(projectName);
	}

	@Override
	public boolean visit(MethodDeclaration method) {
		if (isSerializationMethodDec(method)) {
			String methodName = Util.getMethodFQN(projectName, method);
			this.serializableMethodNames.add(methodName);
		} else if (isCloneableMethod(method.getName())) {
			String methodName = Util.getMethodFQN(projectName, method);
			this.cloneableMethods.put(methodName, method);
		} else if (method.isConstructor()) {
			String methodName = Util.getMethodFQN(projectName, method);
			this.constructors.add(methodName);
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

	/**
	 * Check if the method name is "clone" or not.
	 * 
	 * @param methodName
	 * @return
	 */
	private boolean isCloneableMethod(SimpleName methodName) {
		if (methodName.toString().equals("clone"))
			return true;
		else
			return false;
	}

	public HashSet<String> getSerializableMethodNames() {
		return this.serializableMethodNames;
	}

	public HashMap<String, MethodDeclaration> getCloneableMethods() {
		return this.cloneableMethods;
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
