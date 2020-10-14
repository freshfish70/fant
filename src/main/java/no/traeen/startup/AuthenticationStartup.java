package no.traeen.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import no.traeen.lib.users.Group;

/**
 * Startup actions for authentication that are executed on server start.
 */
@Singleton
@Startup
public class AuthenticationStartup {
	@PersistenceContext
	EntityManager em;

	@PostConstruct
	public void init() {
		persistUsergroups();
	}

	/**
	 * Makes sure that our user grous are added to the database.
	 */
	public void persistUsergroups() {
		// Thread.sleep(5000);
		long groups = (long) em.createQuery("SELECT count(g.name) from Group g").getSingleResult();
		if (groups != Group.GROUPS.length) {
			for (String group : Group.GROUPS) {
				em.merge(new Group(group));
			}
		}
	}
}