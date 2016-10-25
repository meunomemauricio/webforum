package model.users;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PwHashGenerator {

	private static final int ITERATIONS = 65536;
	protected static final int KEY_LENGTH = 512;

	public static String generateHash(String password, String salt) {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
		SecretKeyFactory f;
		byte[] hash = null;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = f.generateSecret(spec).getEncoded();
		} catch (Exception e) {
			throw new RuntimeException("Unexpected Error while trying to generate password hash.");
		}

		Base64.Encoder enc = Base64.getEncoder();
		return enc.encodeToString(hash);
	}

}
