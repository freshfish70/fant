package no.traeen.lib.users;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/* 
Represents a User in the system.
A user has a An ID, email, first name, last name and password.
 */
@Entity
@Table(name = "users")
@NamedQuery(name = User.USER_BY_EMAIL, query = "SELECT e FROM User e WHERE e.email = :email")

public class User implements Serializable {

	public static final String USER_BY_EMAIL = "User.getByEmail";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;

	@Column(nullable = false)
	@NotBlank
	private String firstName;

	@Column(nullable = false)
	@NotBlank
	private String lastName;

	@Column(nullable = false)
	@Email
	private String email;

	@Column(nullable = false)
	@JsonbTransient
	@Size(min = 6)
	private String password;

	@ManyToMany
	@JsonbTransient
	@JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "email", referencedColumnName = "email"), inverseJoinColumns = @JoinColumn(name = "name", referencedColumnName = "name"))
	List<Group> groups;

	public User() {
	}

	public User(String email, String firstName, String lastName, String password) {
		this.setEmail(email);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setPassword(password);
	}

	public BigInteger getId() {
		return this.id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

	public List<Group> getGroups() {
		if (groups == null) {
			groups = new ArrayList<>();
		}
		return groups;
	}

	public void setFirstName(String firstName) {
		if (firstName == null || firstName.isEmpty())
			throw new InvalidParameterException("First name is empty or null");
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		if (lastName == null || lastName.isEmpty())
			throw new InvalidParameterException("Last name is empty or null");
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		if (email == null || email.isEmpty())
			throw new InvalidParameterException("Email is empty or null");
		this.email = email;
	}

	public void setPassword(String password) {
		if (password == null || password.isEmpty())
			throw new InvalidParameterException("Password is empty or null");
		this.password = password;
	}
}