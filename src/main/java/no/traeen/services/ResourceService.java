package no.traeen.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import net.coobird.thumbnailator.Thumbnails;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.security.enterprise.identitystore.IdentityStoreHandler;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import no.traeen.lib.resource.Image;
import no.traeen.lib.store.Item;
import no.traeen.lib.users.Group;
import no.traeen.lib.users.User;

@Path("resource")
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ResourceService {

	@Inject
	@ConfigProperty(name = "photo.storage.path", defaultValue = "images/items")
	String imageStoragePath;

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
	AuthenticationService authenticationService;

	@GET
	@Path("image/{id}")
	@Produces("image/png")
	public Response getImage(@PathParam("id") int id, @QueryParam("width") int width) {
		Image im = em.find(Image.class, BigInteger.valueOf(id));
		if (im != null) {
			StreamingOutput result = (OutputStream os) -> {
				java.nio.file.Path image = Paths.get(imageStoragePath, im.getName());
				if (width == 0) {
					Files.copy(image, os);
					os.flush();
				} else {
					Thumbnails.of(image.toFile()).size(width, width).outputFormat("jpeg").toOutputStream(os);
				}
			};

			// Ask the browser to cache the image for 24 hours
			CacheControl cc = new CacheControl();
			cc.setMaxAge(86400);
			cc.setPrivate(true);

			return Response.ok(result).cacheControl(cc).build();
		} else {
			return Response.status(200).build();
		}
	}

}
