package tests.typestate;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.PBEKeySpec;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class PBEKeySpecTest  extends IDEALCrossingTestingFramework {

	@Override
	protected File getCryptSLFile() {
		return new File("PBEKeySpec.cryptslbin");
	}

	@Test
	public void PBEKeySpecTest1() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000, 128);
		Assertions.assertState(pbe, 0);
	}

	@Test
	public void PBEKeySpecTest2() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000, 128);
		pbe.clearPassword();
		Assertions.assertState(pbe, 1);
	}
	@Test
	public void PBEKeySpecTest3() throws NoSuchAlgorithmException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		Assertions.hasEnsuredPredicate(salt);
		final PBEKeySpec pbe = new PBEKeySpec(new char[] {'p','a','s','s','w','o','r','d'}, salt, 65000, 128);
		Assertions.assertState(pbe, 0);
		pbe.clearPassword();
		Assertions.assertState(pbe, 1);
	}
	
}
