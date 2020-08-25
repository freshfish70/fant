package no.traeen.lib.users;

import java.security.InvalidParameterException;

/* 
Represents a User in the system.
A user has a name, password.
 */
public class User {

	private String name;

	private String password;

	public User(String name, String password) {
		this.setName(name);
		this.setPassword(password);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null || name.isEmpty())
			throw new InvalidParameterException("Name is empty or null");
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		if (password == null || password.isEmpty())
			throw new InvalidParameterException("Password is empty or null");
		this.password = password;
	}
}