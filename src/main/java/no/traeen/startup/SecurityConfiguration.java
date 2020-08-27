package no.traeen.startup;

import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

import org.eclipse.microprofile.auth.LoginConfig;

/* 
	Sets application security settings / configurations
*/

@DatabaseIdentityStoreDefinition(callerQuery = "select password from users where email = ?", groupsQuery = "select name from user_groups where email = ?", priority = 80)
// Sets authentication to JWT, using fant issuer
@LoginConfig(authMethod = "MP-JWT", realmName = "fant")
public class SecurityConfiguration {
}