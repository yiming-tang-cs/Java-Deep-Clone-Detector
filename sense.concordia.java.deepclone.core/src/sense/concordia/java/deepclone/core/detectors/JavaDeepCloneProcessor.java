package sense.concordia.java.deepclone.core.detectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVPrinter;
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

import sense.concordia.java.deepclone.core.util.LoggerNames;
import sense.concordia.java.deepclone.core.util.PrintUtil;
import sense.concordia.java.deepclone.core.util.TimeCollector;

@SuppressWarnings("restriction")
public class JavaDeepCloneProcessor {

	private static final Logger LOGGER = Logger.getLogger(LoggerNames.LOGGER_NAME);

	private IJavaProject[] javaProjects;

	// Store all related method declarations.
	private HashSet<String> cloneableMethods = new HashSet<>();
	private HashSet<String> serializableMethodNames = new HashSet<>();
	private HashSet<String> constructors = new HashSet<>();

	private HashSet<String> cloneableMethodsAST = new HashSet<>();
	private HashSet<String> serializableMethodNamesAST = new HashSet<>();
	private HashSet<String> constructorsAST = new HashSet<>();

	private HashMap<String, Double> subjectToTime = new HashMap<>();

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
	 * @throws IOException
	 */
	public RefactoringStatus process() throws JavaModelException, IOException {

		final RefactoringStatus status = new RefactoringStatus();

		// Print results into a CSV file.
		CSVPrinter resultPrinter = PrintUtil.createCSVPrinter("result.csv", "subject", "clone method", "clone type",
				"file", "source code line", "enclosing method", "time (s)");
		CSVPrinter summaryPrinter = PrintUtil.createCSVPrinter("summary.csv", "subject", "clone methods", "time (s)");

		int projectSize = this.getJavaProjects().length;
		int projectCounting = 1;

		///////////////////// Get all related method declarations from source code
		LOGGER.info("-----------Start to preprocessing!-----------");
		for (IJavaProject jproj : this.getJavaProjects()) {

			// collect running time.
			TimeCollector resultsTimeCollector = new TimeCollector();
			resultsTimeCollector.start();

			// A detector to scan all method declarations.
			JavaMethodDeclarationDetector methodDeclarationDetector = new JavaMethodDeclarationDetector();
			acceptDetector(jproj, methodDeclarationDetector);

			resultsTimeCollector.stop();
			subjectToTime.put(jproj.getElementName(), resultsTimeCollector.getCollectedTime());

			this.serializableMethodNames.addAll(methodDeclarationDetector.getSerializableMethodNames());
			this.cloneableMethods.addAll(methodDeclarationDetector.getCloneableMethods());
			this.constructors.addAll(methodDeclarationDetector.getConstructors());

			this.serializableMethodNamesAST.addAll(methodDeclarationDetector.getSerializableMethodNamesAST());
			this.cloneableMethodsAST.addAll(methodDeclarationDetector.getCloneableMethodsAST());
			this.constructorsAST.addAll(methodDeclarationDetector.getConstructorsAST());

		}
		LOGGER.info("-----------End to preprocessing!-----------");

		///////////////////// Start to evaluate
		for (IJavaProject jproj : this.getJavaProjects()) {

			LOGGER.info("-----------Start to detect [" + projectCounting + "/" + projectSize + "] "
					+ jproj.getElementName() + "!-----------");
			// collect running time.
			TimeCollector resultsTimeCollector = new TimeCollector();
			resultsTimeCollector.start();

			// A detector to detect method invocation for deep clone.
			JavaDeepCloneDetector detector = new JavaDeepCloneDetector(this.serializableMethodNames,
					this.cloneableMethods, this.constructors, this.serializableMethodNamesAST, this.cloneableMethodsAST,
					this.constructorsAST);
			acceptDetector(jproj, detector);

			// Get results and print them into a CSV file.
			HashSet<JavaDeepCloneResult> results = detector.getResults();
			if (!results.isEmpty()) {
				for (JavaDeepCloneResult r : results) {
					resultPrinter.printRecord(r.getSubject(), r.getMethodInvocation(), r.getType(), r.getFile(),
							r.getLine(), r.getEnclosingMethod());
				}
			}
			resultsTimeCollector.stop();
			summaryPrinter.printRecord(jproj.getElementName(), results.size(),
					resultsTimeCollector.getCollectedTime() + subjectToTime.get(jproj.getElementName()));

			LOGGER.info("-----------End to detect [" + projectCounting + "/" + projectSize + "] "
					+ jproj.getElementName() + "!-----------");
			projectCounting++;
		}

		resultPrinter.close();
		summaryPrinter.close();

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

	public HashSet<String> getSerializableMethodNames() {
		return serializableMethodNames;
	}

	public void setSerializableMethodNames(HashSet<String> serializableMethodNames) {
		this.serializableMethodNames = serializableMethodNames;
	}

	public HashSet<String> getCloneableMethods() {
		return cloneableMethods;
	}

	public void setCloneableMethods(HashSet<String> cloneableMethods) {
		this.cloneableMethods = cloneableMethods;
	}

	public HashMap<String, Double> getSubjectToTime() {
		return subjectToTime;
	}

	public void setSubjectToTime(HashMap<String, Double> subjectToTime) {
		this.subjectToTime = subjectToTime;
	}

	public HashSet<String> getConstructors() {
		return constructors;
	}

	public void setConstructors(HashSet<String> constructors) {
		this.constructors = constructors;
	}

	public HashSet<String> getCloneableMethodsAST() {
		return cloneableMethodsAST;
	}

	public void setCloneableMethodsAST(HashSet<String> cloneableMethodsAST) {
		this.cloneableMethodsAST = cloneableMethodsAST;
	}

	public HashSet<String> getSerializableMethodNamesAST() {
		return serializableMethodNamesAST;
	}

	public void setSerializableMethodNamesAST(HashSet<String> serializableMethodNamesAST) {
		this.serializableMethodNamesAST = serializableMethodNamesAST;
	}

	public HashSet<String> getConstructorsAST() {
		return constructorsAST;
	}

	public void setConstructorsAST(HashSet<String> constructorsAST) {
		this.constructorsAST = constructorsAST;
	}

}
