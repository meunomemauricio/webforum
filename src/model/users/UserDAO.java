package model.users;

import java.util.List;

public interface UserDAO {
	   public void register(User u, String password) throws RegistrationError;
	   public User retrieve(String login) throws AuthenticationError;
	   public void authenticate(String login, String password) throws AuthenticationError;
	   public void addPoints(String login, int points) throws AuthenticationError;
	   public List<User> rankUsers();
}