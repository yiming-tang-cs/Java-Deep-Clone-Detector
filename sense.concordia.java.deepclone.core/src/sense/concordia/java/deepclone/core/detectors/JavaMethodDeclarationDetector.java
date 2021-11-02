package sense.concordia.java.deepclone.core.detectors;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

public class JavaMethodDeclarationDetector extends ASTVisitor {

	private HashSet<String> serializableMethodNames = new HashSet<>();

	private HashMap<String, MethodDeclaration> cloneableMethods = new HashMap<>();

	@Override
	public boolean visit(MethodDeclaration method) {
		if (isSerializationMethodDec(method))
			this.serializableMethodNames.add(method.getName().getFullyQualifiedName());

		if (isCloneableMethod(method.getName()))
			this.cloneableMethods.put(method.getName().getFullyQualifiedName(), method);

		return super.visit(method);
	}

	private boolean isSerializationMethodDec(MethodDeclaration method) {
		String methodBody = method.getBody().toString();
		if (methodBody.contains("ObjectOutputStream") && methodBody.contains("ObjectInputStream")
				&& methodBody.contains("ByteArrayOutputStream") && methodBody.contains("ByteArrayInputStream"))
			return true;
		else
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
}
