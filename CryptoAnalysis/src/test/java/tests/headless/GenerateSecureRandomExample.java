package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.IncompleteOperationError;
import test.IDEALCrossingTestingFramework;

public class GenerateSecureRandomExample extends AbstractHeadlessTest {
	
	@Test
	public void generateSecureRandomExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/GenerateSecureRandom").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		
		scanner.exec();
		assertErrors();
	}
}
