package sense.concordia.java.deepclone.tests;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;

import java.util.Set;
import java.util.EnumSet;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneType;
import sense.concordia.java.deepclone.core.detectors.JavaMethodDeclarationDetector;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneResult;
import sense.concordia.java.deepclone.core.detectors.JavaDeepCloneDetector;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.tests.refactoring.GenericRefactoringTest;
import org.eclipse.jdt.ui.tests.refactoring.rules.RefactoringTestSetup;

@SuppressWarnings("restriction")
public class CloneDetectionTest extends GenericRefactoringTest {

	private static final String REFACTORING_PATH = "JavaDeepClone/";
	private static final String RESOURCE_PATH = "resources";

	public CloneDetectionTest() {
		rts = new RefactoringTestSetup();
	}

	protected CloneDetectionTest(RefactoringTestSetup rts) {
		this.rts = rts;
	}

	protected String getRefactoringPath() {
		return REFACTORING_PATH;
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
	 * Compile the test case
	 */
	protected static boolean compiles(String source, Path path) throws IOException {
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

	/**
	 * Core test method.
	 * 
	 * @param expectedResults
	 * @throws Exception
	 */
	private void helper(CloneDetectionExpectedResult... expectedResults) throws Exception {
		ICompilationUnit cu = this.createCUfromTestFile(getPackageP(), "A");

		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setResolveBindings(true);
		parser.setSource(cu);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		ASTNode ast = parser.createAST(new NullProgressMonitor());

		JavaMethodDeclarationDetector methodDeclarationDetector = new JavaMethodDeclarationDetector();
		ast.accept(methodDeclarationDetector);
		JavaDeepCloneDetector detector = new JavaDeepCloneDetector(
				methodDeclarationDetector.getSerializableMethodNames(), methodDeclarationDetector.getCloneableMethods(),
				methodDeclarationDetector.getConstructors());
		ast.accept(detector);

		// Get sets of actual results.
		Set<JavaDeepCloneResult> results = detector.getResults();

		// Check the result size.
		assertEquals("Result size is unexpected!", expectedResults.length, results.size());

		Set<JavaDeepCloneType> types = results.stream().map(r -> r.getType()).collect(Collectors.toSet());
		Set<String> cloneLocations = results.stream()
				.map(r -> r.getFile().substring(r.getFile().lastIndexOf('/') + 1) + ": " + r.getLine())
				.collect(Collectors.toSet());

		for (CloneDetectionExpectedResult expectedResult : expectedResults) {
			assertEquals(expectedResult.getTypes(), types);
			assertEquals(expectedResult.getCloneLocations(), cloneLocations);
		}

	}

	@Test
	public void testCloneableInterface() throws Exception {
		this.helper();
	}

	@Test
	public void testHashMap() throws Exception {
		this.helper();
	}

	@Test
	public void testCloneableInterface2() throws Exception {
		this.helper(new CloneDetectionExpectedResult(EnumSet.of(JavaDeepCloneType.CLONE_METHOD),
				Collections.singleton("A.java: 61")));
	}

	@Test
	public void testCloneContructor() throws Exception {
		this.helper(new CloneDetectionExpectedResult(EnumSet.of(JavaDeepCloneType.CLONE_CONSTRUCTOR),
				Collections.singleton("A.java: 35")));
	}

	@Test
	public void testSerialization() throws Exception {
		this.helper(new CloneDetectionExpectedResult(EnumSet.of(JavaDeepCloneType.CLONE_SERIALIZATION),
				Collections.singleton("A.java: 86")));
	}

	@Test
	public void testSerialization2() throws Exception {
		this.helper(new CloneDetectionExpectedResult(EnumSet.of(JavaDeepCloneType.CLONE_SERIALIZATION),
				Collections.singleton("A.java: 55")));
	}

	@Test
	public void testSerialization3() throws Exception {
		this.helper(new CloneDetectionExpectedResult(EnumSet.of(JavaDeepCloneType.CLONE_SERIALIZATION),
				Collections.singleton("A.java: 73")));
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
