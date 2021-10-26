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

	/**
	 * Check whether a method is used to clone objects.
	 * May need to improve.
	 * @param method
	 * @return True/False
	 */
	private boolean isCloneMethod(MethodInvocation method) {
		if (method.getName().toString().equals("clone"))
			return true;

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
