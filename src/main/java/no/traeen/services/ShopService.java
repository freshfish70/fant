package no.traeen.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import javax.ws.rs.core.Response;

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

@Path("shop")
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
// @MultipartConfig()
public class ShopService {

	private final String IMAGE_PATH = "images/items/";
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

	@POST
	@Path("buyitem")
	@RolesAllowed(value = { Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME })
	public Response purchaseItem(@HeaderParam("itemId") Integer itemId) {

		User user = authenticationService.getCurrentUser(tk.getName());
		Item item = em.find(Item.class, BigInteger.valueOf(itemId));
		System.out.println(item);
		System.out.println(user);
		if (!(item.getSeller().getId().equals(user.getId()))) {
			item.setBuyer(user);
			em.persist(item);
		}
		return Response.ok().build();
	}

	@POST
	@Path("additem")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON })
	@RolesAllowed({ Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME })
	public Response addItem(@HeaderParam("name") String name, @HeaderParam("description") String description,
			@HeaderParam("price") float price, IMultipartBody multipartBody, @Context HttpServletRequest re) {

		User user = authenticationService.getCurrentUser(tk.getName());
		Item item = new Item(name, description, price, user);
		Set<Image> images = saveImages(multipartBody);
		for (Image image : images) {
			image.setOwner(item);
		}
		item.setImage(images);
		em.persist(item);
		return Response.ok().build();
	}

	private Set<Image> saveImages(IMultipartBody multipartBody) {

		List<IAttachment> attachments = multipartBody.getAllAttachments();
		InputStream stream = null;
		Set<Image> images = new HashSet<>();

		File directory = new File(IMAGE_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		for (Iterator<IAttachment> it = attachments.iterator(); it.hasNext();) {
			try {
				IAttachment attachment = it.next();
				if (attachment == null) {
					continue;
				}
				DataHandler dataHandler = attachment.getDataHandler();
				stream = dataHandler.getInputStream();

				MultivaluedMap<String, String> map = attachment.getHeaders();

				String fileName = null;
				String formElementName = null;
				String[] contentDisposition = map.getFirst("Content-Disposition").split(";");

				for (String tempName : contentDisposition) {
					try {
						String[] names = tempName.split("=");
						formElementName = names[1].trim().replaceAll("\"", "");
						if ((tempName.trim().startsWith("filename"))) {
							fileName = formElementName;
						}
					} catch (Exception e) {
						continue;
					}
				}
				if (fileName != null) {
					String pid = UUID.randomUUID().toString();
					String fullFileName = IMAGE_PATH + pid + "-" + fileName.trim();
					Image image = new Image();
					MediaType mediatype = attachment.getContentType();
					image.setMimeType(mediatype.toString());
					Files.copy(stream, Paths.get(fullFileName));
					images.add(image);

				}
				if (stream != null) {
					System.out.println("Closing stream");
					stream.close();
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return images;
	}

	@GET
	@Path("getitem")
	public Response getItem(@HeaderParam("id") Integer id) {
		Object itemId = BigInteger.valueOf(id);
		Item item = em.find(Item.class, (itemId));
		return Response.ok(item).build();
	}

	@DELETE
	@Path("deleteitem")
	@RolesAllowed(value = { Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME })
	public Response deleteItem(@HeaderParam("itemId") Integer itemId) {
		try {
			User user = authenticationService.getCurrentUser(tk.getName());
			em.createNamedQuery(Item.DELETE_BY_ID, Item.class).setParameter("id", BigInteger.valueOf(itemId).intValue())
					.setParameter("seller", user).executeUpdate();
		} catch (Exception e) {

		}
		return Response.ok().build();
	}

}