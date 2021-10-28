package sense.concordia.java.deepclone.core.detectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

@SuppressWarnings("restriction")
public class JavaDeepCloneResult {
	private JavaDeepCloneType type;
	private String enclosingMethod;
	private String subject;
	private String file;
	private int line;

	public JavaDeepCloneResult(MethodInvocation method, JavaDeepCloneType type) {
		this.setType(type);
		CompilationUnit cu = (CompilationUnit) method.getRoot();
		this.setSubject(cu.getJavaElement().getJavaProject().getElementName());
		this.setFile(cu.getJavaElement().getPath().makeAbsolute().toString());
		this.setLine(cu.getLineNumber(method.getStartPosition()));

		this.setEnclosingMethod(
				((MethodDeclaration) ASTNodes.getParent(method, ASTNode.METHOD_DECLARATION)).toString());
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

}
