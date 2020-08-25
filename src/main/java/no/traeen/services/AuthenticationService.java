package no.traeen.services;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("authentication")
public class AuthenticationService {
	
	@POST
	@Path("login")
	public Response login(String userid, String password) {
		return Response.ok("login").build();
	}
	
	@POST
	@Path("create")
	public Response createUser(String userid, String password) {
		return Response.ok("created user").build();
	}

	// @GET
	// public User getCurrentUser() {
	// 	return Response.ok("You are user").build();
	// }

	// /** Change password of current user or any user if current user has the role of
	// administrator */
	// public Response changePassword(String userid, String password) {

	// }
}
