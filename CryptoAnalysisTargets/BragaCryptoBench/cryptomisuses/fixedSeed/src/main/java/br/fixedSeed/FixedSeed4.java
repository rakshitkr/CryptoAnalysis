package br.fixedSeed;

import java.security.SecureRandom;

public final class FixedSeed4 {

	public static void main(String[] args) {
		try {
			byte[] fixedSeed = org.alexmbraga.utils.U.x2b("0123456789ABCDEF0123456789ABCDEF");

			SecureRandom r3 = SecureRandom.getInstanceStrong();
			r3.setSeed(10);
			SecureRandom r4 = SecureRandom.getInstanceStrong();
			r4.setSeed(fixedSeed);
		} catch (Exception e) {
		}
	}

}
