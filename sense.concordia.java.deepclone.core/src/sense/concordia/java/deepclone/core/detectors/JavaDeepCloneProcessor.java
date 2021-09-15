package sense.concordia.java.deepclone.core.detectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

@SuppressWarnings("restriction")
public class JavaDeepCloneProcessor {
	
	private IJavaProject[] javaProjects;
	
	public RefactoringStatus checkFinalConditions() throws JavaModelException {

		final RefactoringStatus status = new RefactoringStatus();

		for (IJavaProject jproj : this.getJavaProjects()) {

			JavaDeepCloneDetector detector = new JavaDeepCloneDetector();

			IPackageFragmentRoot[] roots = jproj.getPackageFragmentRoots();
			for (IPackageFragmentRoot root : roots) {
				IJavaElement[] children = root.getChildren();
				for (IJavaElement child : children)
					if (child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
						IPackageFragment fragment = (IPackageFragment) child;
						ICompilationUnit[] units = fragment.getCompilationUnits();
						for (ICompilationUnit unit : units) {
							CompilationUnit compilationUnit = RefactoringASTParser.parseWithASTProvider(unit, true, null);
							compilationUnit.accept(detector);
						}
					}
			}

			detector.detect();
		}

//		// get the status of each J.U.L log invocation.
//		RefactoringStatus collectedStatus = this.getLogInvocationSet().stream().map(LogInvocation::getStatus)
//				.collect(() -> new RefactoringStatus(), (a, b) -> a.merge(b), (a, b) -> a.merge(b));
//
//		status.merge(collectedStatus);
//
//
//		if (!status.hasFatalError()) {
//			if (logInvocationSet.isEmpty() && logInvocationSlf4j.isEmpty()) {
//				status.addWarning(Messages.NoInputLogInvs);
//			}
//		}

		return status;
	}

	private IJavaProject[] getJavaProjects() {
		return this.javaProjects;
	}

}
