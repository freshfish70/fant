package no.traeen.startup;

import javax.annotation.security.DeclareRoles;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

import org.eclipse.microprofile.auth.LoginConfig;

import no.traeen.lib.users.Group;
/* 
	Sets application security settings / configurations
*/

// Adds credential validation queries to validation store. 
@DatabaseIdentityStoreDefinition(callerQuery = "select password from users where email = ?", groupsQuery = "select name from user_groups where email = ?", priority = 80)

// Roles allowed for authentication
@DeclareRoles({ Group.USER_GROUP_NAME, Group.USER_GROUP_NAME })

// Sets authentication to JWT, using fant issuer
@LoginConfig(authMethod = "MP-JWT", realmName = "fant")
public class SecurityConfiguration {
}