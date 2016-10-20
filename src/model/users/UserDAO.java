package model.users;

import java.util.List;

public interface UserDAO {
	   public void insert(User u) throws RegistrationError;
	   public User retrieve(String login);
	   public boolean authenticate(String login, String senha);
	   public void addPoints(String login, int pontos) throws InvalidUserError;
	   public List<User> rankUsers();
}