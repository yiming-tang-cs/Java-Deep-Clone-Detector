package sense.concordia.java.deepclone.core.detectors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class JavaDeepCloneDetector extends ASTVisitor {

	private HashSet<JavaDeepCloneResult> results = new HashSet<>();

	public void detect() {
		results.forEach(r -> {
			System.out.println(r.getSubject() + ": " + r.getType() + ", " + r.getFile() + ", " + r.getLine());
			System.out.println(r.getEnclosingMethod());
			System.out.println();
		});
	}

	@Override
	public boolean visit(MethodInvocation method) {

		if (isCloneMethod(method))
			this.addResult(method, JavaDeepCloneType.CLONE_METHOD);

		return super.visit(method);
	}

	/**
	 * Check whether a method is used to clone objects. May need to improve.
	 * 
	 * @param method
	 * @return True/False
	 */
	private boolean isCloneMethod(MethodInvocation method) {
		if (method.getName().toString().equals("clone"))
			return true;

		return false;
	}

	public HashSet<JavaDeepCloneResult> getResults() {
		return this.results;
	}

	public void setResults(HashSet<JavaDeepCloneResult> results) {
		this.results = results;
	}

	private void addResult(MethodInvocation method, JavaDeepCloneType type) {
		JavaDeepCloneResult newResult = new JavaDeepCloneResult(method, type);
		results.add(newResult);
	}

}
