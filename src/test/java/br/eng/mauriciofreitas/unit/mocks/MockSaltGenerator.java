package br.eng.mauriciofreitas.unit.mocks;

import br.eng.mauriciofreitas.model.users.SaltGenerator;

public class MockSaltGenerator implements SaltGenerator {

	@Override
	public String generate() {
		return "salt";
	}

}
