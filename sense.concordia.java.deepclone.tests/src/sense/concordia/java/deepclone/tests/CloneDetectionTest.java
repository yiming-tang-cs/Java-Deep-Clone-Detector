package sense.concordia.java.deepclone.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.ui.tests.refactoring.GenericRefactoringTest;
import org.eclipse.jdt.ui.tests.refactoring.rules.RefactoringTestSetup;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneDetector;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneResult;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneType;

@SuppressWarnings("restriction")
public class CloneDetectionTest extends GenericRefactoringTest {
	
	private static final String REFACTORING_PATH = "JavaDeepClone/";
	
	@Override
	public String getRefactoringPath() {
		return REFACTORING_PATH;
	}
	
	public CloneDetectionTest() {
		rts= new RefactoringTestSetup();
	}
	
	/**
	 * Compile the test case
	 */
	private static boolean compiles(String source, Path path) throws IOException {
		// Save source in .java file.
		File sourceFile = new File(path.toFile(), "bin/p/A.java");
		sourceFile.getParentFile().mkdirs();
		Files.write(sourceFile.toPath(), source.getBytes());

		// Compile source file.
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		boolean compileSuccess = compiler.run(null, null, null, sourceFile.getPath()) == 0;

		sourceFile.delete();
		return compileSuccess;
	}
	
	@Override
	protected ICompilationUnit createCUfromTestFile(IPackageFragment pack, String cuName) throws Exception {

		ICompilationUnit unit = super.createCUfromTestFile(pack, cuName);
		
		if (!unit.isStructureKnown())
			throw new IllegalArgumentException(cuName + " has structural errors.");

		Path directory = Paths.get(unit.getParent().getParent().getParent().getResource().getLocation().toString());

		assertTrue("Should compile the testing cases:", compiles(unit.getSource(), directory));

		return unit;
	}

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

		// Get sets of actual results.
		Set<JavaDeepCloneResult> results = detector.getResults();
		Set<JavaDeepCloneType> types = results.stream().map(r -> r.getType()).collect(Collectors.toSet());
		Set<String> cloneLocations = results.stream().map(r -> r.getFile() + ": " + r.getLine())
				.collect(Collectors.toSet());

		assertEquals("Result is empty!", 0, results.size());
		assertEquals("Result size is unexpected!", expectedResults.length, results.size());

		for (CloneDetectionExpectedResult expectedResult : expectedResults) {
			assertEquals(expectedResult.getTypes(), types);
			assertEquals(expectedResult.getCloneLocations(), cloneLocations);
		}

	}

	@Test
	public void testCloneableInterface() throws Exception {
		this.helper(new CloneDetectionExpectedResult(EnumSet.of(JavaDeepCloneType.CLONE_METHOD),
				Collections.singleton("")));
	}

}
