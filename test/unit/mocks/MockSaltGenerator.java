package unit.mocks;

import model.users.SaltGenerator;

public class MockSaltGenerator implements SaltGenerator {

	@Override
	public String generate() {
		return "salt";
	}

}
