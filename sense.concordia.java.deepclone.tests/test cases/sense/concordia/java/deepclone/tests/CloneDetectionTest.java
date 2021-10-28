package sense.concordia.java.deepclone.tests;

import org.junit.Rule;
import org.junit.Test;

import java.util.Set;
import java.util.EnumSet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.Collections;

import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneDetector;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneResult;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneType;

import static org.junit.Assert.*;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.tests.refactoring.GenericRefactoringTest;
import org.eclipse.jdt.ui.tests.refactoring.infra.RefactoringTestPlugin;
import org.eclipse.jdt.ui.tests.refactoring.rules.RefactoringTestSetup;

@SuppressWarnings("restriction")
public class CloneDetectionTest extends GenericRefactoringTest {

	private static final String REFACTORING_PATH = "JavaDeepClone/";
	private static final String RESOURCE_PATH = "resources";

	public CloneDetectionTest() {
		rts = new RefactoringTestSetup();
	}
	
	protected CloneDetectionTest(RefactoringTestSetup rts) {
		this.rts= rts;
	}

	protected String getRefactoringPath() {
		return REFACTORING_PATH;
	}

	/**
	 * Core test method.
	 * 
	 * @param expectedResults
	 * @throws Exception
	 */
	private void helper(CloneDetectionExpectedResult... expectedResults) throws Exception {
		ICompilationUnit cu = createCUfromTestFile(getPackageP(), "A");

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
	
	/*
	 * This method could fix the issue that the bundle has no entry.
	 */
	@Override
	public String getFileContents(String fileName) throws IOException {
		Path absolutePath = getAbsolutePath(fileName);
		byte[] encoded = Files.readAllBytes(absolutePath);
		return new String(encoded, Charset.defaultCharset());
	}

	private static Path getAbsolutePath(String fileName) {
		Path path = Paths.get(RESOURCE_PATH, fileName);
		Path absolutePath = path.toAbsolutePath();
		return absolutePath;
	}

}
