package sense.concordia.java.deepclone.core.detectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

@SuppressWarnings("restriction")
public class JavaDeepCloneProcessor {

	private IJavaProject[] javaProjects;

	/**
	 * Constructor.
	 * 
	 * @param javaProjects: the selected java projects.
	 */
	public JavaDeepCloneProcessor(IJavaProject[] javaProjects) {
		this.javaProjects = javaProjects;
	}

	/**
	 * The method to process the selected projects. It analyzes the AST of each
	 * files and visits each AST node.
	 * 
	 * @return a status of detection.
	 * @throws JavaModelException
	 */
	public RefactoringStatus process() throws JavaModelException {

		final RefactoringStatus status = new RefactoringStatus();

		for (IJavaProject jproj : this.getJavaProjects()) {

			// A detector to scan all method declarations.
			JavaMethodDeclarationDetector methodDeclarationDetector = new JavaMethodDeclarationDetector();
			acceptDetector(jproj, methodDeclarationDetector);

			// A detector to detect method invocation for deep clone.
			JavaDeepCloneDetector detector = new JavaDeepCloneDetector(
					methodDeclarationDetector.getSerializableMethodNames(),
					methodDeclarationDetector.getCloneableMethods());
			acceptDetector(jproj, detector);
			detector.detect();
		}

		return status;
	}

	private void acceptDetector(IJavaProject jproj, ASTVisitor visitor) throws JavaModelException {
		IPackageFragmentRoot[] roots = jproj.getPackageFragmentRoots();
		for (IPackageFragmentRoot root : roots) {
			IJavaElement[] children = root.getChildren();
			for (IJavaElement child : children)
				if (child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
					IPackageFragment fragment = (IPackageFragment) child;
					ICompilationUnit[] units = fragment.getCompilationUnits();
					for (ICompilationUnit unit : units) {
						CompilationUnit compilationUnit = RefactoringASTParser.parseWithASTProvider(unit, true, null);
						compilationUnit.accept(visitor);
					}
				}
		}
	}

	private IJavaProject[] getJavaProjects() {
		return this.javaProjects;
	}

}
