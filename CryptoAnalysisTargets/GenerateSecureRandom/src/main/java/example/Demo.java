package example;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class Demo {
	public void generateSecureRandom() throws GeneralSecurityException {
        byte[] seed = {1, 3};
        SecureRandom secureRandom = new SecureRandom(seed);
    }
}
