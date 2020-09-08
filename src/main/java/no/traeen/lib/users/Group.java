package no.traeen.lib.users;

import java.util.List;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/*
	Represents a user group in the application. A user group is
	used for permissions, where each group has different permissions when 
	accessing resources. 
*/
@Entity
@Table(name = "Groups")
public class Group {

	public static final String USER_GROUP_NAME = "user";
	public static final String ADMIN_GROUP_NAME = "admin";
	public static final String[] GROUPS = { USER_GROUP_NAME, ADMIN_GROUP_NAME };

	@Id
	String name;

	@JsonbTransient
	@ManyToMany
	@JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "name", referencedColumnName = "name"), inverseJoinColumns = @JoinColumn(name = "email", referencedColumnName = "email"))
	List<User> users;

	public Group() {
	}

	public Group(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}