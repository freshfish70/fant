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
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

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
import no.traeen.lib.response.DataResponse;
import no.traeen.lib.response.ErrorResponse;
import no.traeen.lib.response.errors.ErrorMessage;
import no.traeen.lib.store.Item;
import no.traeen.lib.users.Group;
import no.traeen.lib.users.User;

@Path("shop")
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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
	public Response purchaseItem(@HeaderParam("id") int itemId) {

		ResponseBuilder resp = null;
		User user = authenticationService.getCurrentUser(tk.getName());
		Item item = em.find(Item.class, BigInteger.valueOf(itemId));
		if (item == null) {
			ErrorMessage message = new ErrorMessage("No item");
			resp = Response.ok(new ErrorResponse(message));
		} else if (!(item.getSeller().getId().equals(user.getId()))) {
			item.setBuyer(user);
			item.setSold(true);
			em.persist(item);
			Response.ok(new DataResponse("ok"));
		} else {
			ErrorMessage message = new ErrorMessage("Cant buy own item");
			resp = Response.ok(new ErrorResponse(message));
		}
		return resp.build();
	}

	/**
	 * Returns all items on given pagination page. A page has a page size.
	 * 
	 * @param page the page to get items from
	 * @return returns Reponse
	 */
	@GET
	@Path("getitems")
	public Response getItems(@QueryParam("page") int page) {
		ResponseBuilder resp;
		try {
			Long totalItems = em.createNamedQuery(Item.COUNT_TOTAL_ITEMS, Long.class).getSingleResult();
			int pageSize = 10;
			int totalPages = (int) Math.ceil(totalItems / pageSize);
			int lower = 0;
			if (page < 1) {
				page = 1;
			}
			if (page >= 1) {
				lower = (page - 1) * pageSize;
				if (lower > totalItems) {
					lower = totalPages * pageSize;
				}
			}

			TypedQuery<Item> tq = em.createNamedQuery(Item.GET_ALL_DESC, Item.class);
			tq.setMaxResults(pageSize);
			tq.setFirstResult(lower);
			List<Item> items = tq.getResultList();
			resp = Response.ok(new DataResponse(items).getResponse());
		} catch (Exception e) {
			resp = Response.ok(new ErrorResponse(new ErrorMessage("Could not get items")).getResponse())
					.status(Status.NOT_FOUND);
		}

		return resp.build();
	}

	/**
	 * Adds a new items for sale. All items requires a name/title, a description, a
	 * price, and image(s).
	 * 
	 * @param name          the name of the item
	 * @param description   decription of the item
	 * @param price         the price to sell for
	 * @param multipartBody attachements
	 * @param request       the http request
	 * @return return Response
	 */
	@POST
	@Path("additem")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON })
	@RolesAllowed({ Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME })
	public Response addItem(@HeaderParam("name") String name, @HeaderParam("description") String description,
			@HeaderParam("price") float price, IMultipartBody multipartBody, @Context HttpServletRequest request) {
		ResponseBuilder resp;
		try {
			User user = authenticationService.getCurrentUser(tk.getName());
			Item item = new Item(name, description, price, user);
			Set<Image> images = saveImages(multipartBody);
			for (Image image : images) {
				image.setOwner(item);
			}
			item.setImage(images);
			em.persist(item);
			resp = Response.ok(new DataResponse().getResponse());
		} catch (Exception e) {
			resp = Response.ok(new ErrorResponse(new ErrorMessage("Could not store item")).getResponse());
		}

		return resp.build();
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
					String newName = pid + "-" + fileName.trim();
					String fullFileName = IMAGE_PATH + newName;
					Image image = new Image();
					MediaType mediatype = attachment.getContentType();

					long size = Files.copy(stream, Paths.get(fullFileName));
					image.setName(newName);
					image.setMimeType(mediatype.toString());
					image.setSize(size);
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

	/**
	 * Returns an item with given id, if it is found. Else return nothing, with
	 * error.
	 * 
	 * @param id the id of the item
	 * @return return Response
	 */
	@GET
	@Path("getitem")
	public Response getItem(@QueryParam("id") Integer id) {
		ResponseBuilder resp;
		Item item = em.find(Item.class, BigInteger.valueOf(id));
		if (item == null) {
			resp = Response.ok(new ErrorResponse(new ErrorMessage("No items with id " + id)).getResponse());
		} else {
			resp = Response.ok(new DataResponse(item).getResponse());
		}
		return resp.build();
	}

	/**
	 * Deletes an item by id from the store. The item can only be deleted by the
	 * owner of the item.
	 * 
	 * @param itemId the id of the item to delete
	 * @return
	 */
	@DELETE
	@Path("deleteitem")
	@RolesAllowed(value = { Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME })
	public Response deleteItem(@HeaderParam("itemId") Integer itemId) {
		ResponseBuilder resp;
		try {
			User user = authenticationService.getCurrentUser(tk.getName());
			int updated = em.createNamedQuery(Item.DELETE_BY_ID, Item.class)
					.setParameter("id", BigInteger.valueOf(itemId).intValue()).setParameter("seller", user)
					.executeUpdate();
			if (updated > 0) {
				resp = Response.ok(new DataResponse("").getResponse());
			} else {
				resp = Response.ok(new ErrorResponse(new ErrorMessage("No items deleted")).getResponse());
			}
		} catch (Exception e) {
			resp = Response.ok(
					new ErrorResponse(new ErrorMessage("Something want wrong with deleteing item with id " + itemId))
							.getResponse());
		}
		return resp.build();
	}

}