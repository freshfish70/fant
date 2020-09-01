package no.traeen.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.security.enterprise.identitystore.CredentialValidationResult.Status;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import no.traeen.lib.users.Group;
import no.traeen.lib.users.User;

@Path("authentication")
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationService {

	@Inject
	KeyService keyService;

	@Inject
	IdentityStoreHandler identityStoreHandler;

	@Inject
	@ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
	String issuer;

	@PersistenceContext
	EntityManager em;

	@Inject
	JsonWebToken tk;

	@Inject
	PasswordHash hasher;

	/**
	 * Authenticates a user if providing a correct email/password combination.
	 * Returns a JWT token on success else an error response.
	 * 
	 * @param email    the email of the user
	 * @param password the password of the user
	 * @param request  http request
	 * @return JSON Response
	 */
	@POST
	@Path("login")
	public Response login(@HeaderParam("email") String email, @HeaderParam("password") String password,
			@Context HttpServletRequest request) {

		ResponseBuilder response = Response.ok("...");

		try {
			CredentialValidationResult result = identityStoreHandler
					.validate(new UsernamePasswordCredential(email, password));
			if (result.getStatus() == Status.VALID) {
				String token = generateToken(result.getCallerPrincipal().getName(), result.getCallerGroups(), request);
				response = Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}
		} catch (Exception e) {
			response = Response.status(500);
		}

		return response.build();
	}

	/**
	 * 
	 * @param name    name of the subject
	 * @param groups  groups to include in the JWT
	 * @param request http request
	 * @return returns JWT token or empty string
	 */
	private String generateToken(String name, Set<String> groups, HttpServletRequest request) {
		try {
			Date now = new Date();
			Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
			JwtBuilder jb = Jwts.builder().setHeaderParam("typ", "JWT").setHeaderParam("kid", "abc-1234567890")
					.setSubject(name).setId("a-123").claim("iss", issuer).setIssuedAt(now).setExpiration(expiration)
					.claim("upn", name).claim("groups", groups).claim("aud", "aud").claim("auth_time", now)
					.signWith(keyService.getKeyManager().getPrivateKey());
			return jb.compact();
		} catch (InvalidKeyException t) {
			// log.log(Level.SEVERE, "Failed to create token", t);
			// throw new RuntimeException("Failed to create token", t);
		}
		return "";
	}

	@POST
	@Path("create")
	public Response createUser(@HeaderParam("firstname") String firstname, @HeaderParam("lastname") String lastname,
			@HeaderParam("password") String password, @HeaderParam("email") String email) {
		try {
			User user = em.createNamedQuery(User.USER_BY_EMAIL, User.class).setParameter("email", email)
					.getSingleResult();
			// Return error
		} catch (NoResultException e) {
			User newUser = new User(email, firstname, lastname, password);
			Group usergroup = em.find(Group.class, Group.USER_GROUP_NAME);
			newUser.setPassword(hasher.generate(password.toCharArray()));
			newUser.getGroups().add(usergroup);
			em.merge(newUser);
		} catch (PersistenceException e) {
			// Return Error
		}
		return Response.ok().build();

	}

	@POST
	@Path("credentialTest")
	@RolesAllowed("admin")
	public Response credentialTest() {
		return Response.ok(tk.getGroups()).build();
	}

	// public User getCurrentUser() {
	// return new User();
	// }

	// /** Change password of current user or any user if current user has the role
	// of
	// administrator */
	// public Response changePassword(String userid, String password) {

	// }
}
