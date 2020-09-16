package no.traeen.services;

import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;

import net.coobird.thumbnailator.Thumbnails;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.security.enterprise.identitystore.IdentityStoreHandler;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import no.traeen.lib.resource.Image;

@Path("resource")
@Stateless
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

	/**
	 * Returns the image with the given id. If the width parameter is set, it will
	 * scale the image width/height to the specified witdth.
	 * 
	 * @param id    id of the image
	 * @param width desired return width
	 * @return image or 404
	 */
	@GET
	@Path("image/{id}")
	@Produces(MediaType.WILDCARD)
	public Response getImage(@PathParam("id") int id, @QueryParam("width") int width) {
		Image im = em.find(Image.class, BigInteger.valueOf(id));
		if (im != null) {
			String[] split = im.getName().split("[.]");
			String ext = split[split.length - 1];
			StreamingOutput result = (OutputStream os) -> {
				java.nio.file.Path image = Paths.get(imageStoragePath, im.getName());
				if (width == 0) {
					Files.copy(image, os);
					os.flush();
				} else {
					Thumbnails.of(image.toFile()).size(width, width).outputFormat(ext).toOutputStream(os);
				}
			};
			CacheControl cc = new CacheControl();
			cc.setMaxAge(86400);
			cc.setPrivate(true);

			return Response.ok(result).cacheControl(cc).type(im.getMimeType()).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

}
