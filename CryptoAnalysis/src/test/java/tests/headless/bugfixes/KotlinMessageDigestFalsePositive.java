package tests.headless.bugfixes;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import tests.headless.AbstractHeadlessTest;
import tests.headless.MavenProject;

/**
 * Refers to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/271
 */
public class KotlinMessageDigestFalsePositive extends AbstractHeadlessTest  {
	@Test
	public void issue271Kotlin() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Bugfixes/issue271/Kotlin").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		scanner.exec();
		assertErrors();
	}

	@Test
	public void issue271Java() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Bugfixes/issue271/Java").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		scanner.exec();
		assertErrors();
	}
}
