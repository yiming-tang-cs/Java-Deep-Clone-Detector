package sense.concordia.java.deepclone.core.detectors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class JavaDeepCloneDetector extends ASTVisitor {

	private HashSet<JavaDeepCloneResults> results = new HashSet<>();

	public void detect() {
		results.forEach(
				r -> System.out.println(r.getSubject() + ": " + r.getType() + ", " + r.getFile() + ", " + r.getLine()));
	}

	@Override
	public boolean visit(MethodInvocation method) {

		if (isCloneMethod(method))
			this.addResult(method, JavaDeepCloneType.CLONE_METHOD);

		return super.visit(method);
	}

	private boolean isCloneMethod(MethodInvocation method) {
		if (method.getName().equals("clone")) {
			if (method.resolveMethodBinding().getMethodDeclaration().toString().contains("super.clone()"))
				return true;
		}
		return false;
	}

	public HashSet<JavaDeepCloneResults> getResults() {
		return results;
	}

	public void setResults(HashSet<JavaDeepCloneResults> results) {
		this.results = results;
	}

	private void addResult(MethodInvocation method, JavaDeepCloneType type) {
		JavaDeepCloneResults newResult = new JavaDeepCloneResults(method, type);
		results.add(newResult);
	}

}
