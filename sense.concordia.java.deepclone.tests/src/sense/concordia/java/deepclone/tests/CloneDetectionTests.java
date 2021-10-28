package sense.concordia.java.deepclone.tests;

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.ui.tests.refactoring.GenericRefactoringTest;
import org.junit.Assert;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneDetector;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneResult;

@SuppressWarnings("restriction")
public class CloneDetectionTests extends GenericRefactoringTest {

	/**
	 * Core test method.
	 * 
	 * @param expectedResults
	 * @throws Exception
	 */
	private void helper(CloneDetectionExpectedResult... expectedResults) throws Exception {
		// compute the actual results.
		ICompilationUnit cu = this.createCUfromTestFile(this.getPackageP(), "A");

		ASTParser parser = ASTParser.newParser(AST.JLS15);
		parser.setResolveBindings(true);
		parser.setSource(cu);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		ASTNode ast = parser.createAST(new NullProgressMonitor());

		JavaDeepCloneDetector detector = new JavaDeepCloneDetector();
		ast.accept(detector);

		Set<JavaDeepCloneResult> results = detector.getResults();

		Assert.assertNotEquals("Result is empty", 0, results.size());
		
		for (CloneDetectionExpectedResult expectedResult : expectedResults) {
			// Todo
		}

	}
	
	public void testCloneableInterface() throws Exception {
		this.helper(new CloneDetectionExpectedResult());
	}

}
