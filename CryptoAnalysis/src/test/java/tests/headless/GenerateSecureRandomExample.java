package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import test.IDEALCrossingTestingFramework;

public class GenerateSecureRandomExample extends AbstractHeadlessTest {
	
	@Test
	public void generateSecureRandomExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/GenerateSecureRandom").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<example.Demo: void generateSecureRandom()>", RequiredPredicateError.class, 1);
		
		scanner.exec();
		assertErrors();
	}
}
