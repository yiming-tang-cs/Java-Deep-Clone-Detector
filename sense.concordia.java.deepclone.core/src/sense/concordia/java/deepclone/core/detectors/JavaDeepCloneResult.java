package sense.concordia.java.deepclone.core.detectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import sense.concordia.java.deepclone.core.util.Util;

public class JavaDeepCloneResult {
	private JavaDeepCloneType type;
	private String enclosingMethod;
	private String methodInvocation;
	private String subject;
	private String file;
	private int line;

	public JavaDeepCloneResult(ASTNode method, JavaDeepCloneType type) {
		this.setType(type);
		CompilationUnit cu = (CompilationUnit) method.getRoot();
		this.setSubject(cu.getJavaElement().getJavaProject().getElementName());
		this.setFile(cu.getJavaElement().getPath().makeAbsolute().toString());
		this.setLine(cu.getLineNumber(method.getStartPosition()));

		this.setMethodInvocation(method.toString());
		this.setEnclosingMethod(Util.getEnclosingMethod(method));
	}

	public JavaDeepCloneType getType() {
		return type;
	}

	public void setType(JavaDeepCloneType type) {
		this.type = type;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getEnclosingMethod() {
		return enclosingMethod;
	}

	public void setEnclosingMethod(String enclosingMethod) {
		this.enclosingMethod = enclosingMethod;
	}

	public String getMethodInvocation() {
		return methodInvocation;
	}

	public void setMethodInvocation(String methodInvocation) {
		this.methodInvocation = methodInvocation;
	}

}
