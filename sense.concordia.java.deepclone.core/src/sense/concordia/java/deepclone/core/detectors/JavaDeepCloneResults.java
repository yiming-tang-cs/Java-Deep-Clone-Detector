package sense.concordia.java.deepclone.core.detectors;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class JavaDeepCloneResults {
	private JavaDeepCloneType type;
	private String subject;
	private String file;
	private int line;

	public JavaDeepCloneResults(MethodInvocation method, JavaDeepCloneType type) {
		this.setType(type);
		CompilationUnit cu = (CompilationUnit) method.getRoot();
		this.setSubject(cu.getJavaElement().getJavaProject().getElementName());
		this.file = cu.getJavaElement().getPath().makeAbsolute().toString();
		this.line = cu.getLineNumber(method.getStartPosition());
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

}
