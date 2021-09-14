package sense.concordia.java.deepclone.core.detectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class JavaDeepCloneProcessor {
	
	
	
	public RefactoringStatus checkFinalConditions(){

		final RefactoringStatus status = new RefactoringStatus();

//		for (IJavaProject jproj : this.getJavaProjects()) {
//
//			JavaDeepCloneDetector detector = new JavaDeepCloneDetector();
//
//			IPackageFragmentRoot[] roots = jproj.getPackageFragmentRoots();
//			for (IPackageFragmentRoot root : roots) {
//				IJavaElement[] children = root.getChildren();
//				for (IJavaElement child : children)
//					if (child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
//						IPackageFragment fragment = (IPackageFragment) child;
//						ICompilationUnit[] units = fragment.getCompilationUnits();
//						for (ICompilationUnit unit : units) {
//							CompilationUnit compilationUnit = super.getCompilationUnit(unit, monitor);
//							compilationUnit.accept(detector);
//						}
//					}
//			}
//
//			detector.analyze();
//		}

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
		// TODO Auto-generated method stub
		return null;
	}

}
