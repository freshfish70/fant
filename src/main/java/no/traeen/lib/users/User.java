package no.traeen.lib.users;

import java.math.BigInteger;
import java.security.InvalidParameterException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/* 
Represents a User in the system.
A user has a name, password.
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger userid;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	public User() {
	}

	public User(String name, String password) {
		this.setName(name);
		this.setPassword(password);
	}

	public BigInteger getUserid() {
		return this.userid;
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