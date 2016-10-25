package model.users;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureSaltGenerator implements SaltGenerator {

	@Override
	public String generate() {
		byte[] salt = new byte[PwHashGenerator.KEY_LENGTH / 8];
	    (new SecureRandom()).nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

}
