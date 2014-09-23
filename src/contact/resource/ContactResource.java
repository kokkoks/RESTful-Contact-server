package contact.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.expr.NewArray;

import javax.net.ssl.SSLEngineResult.Status;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;

/**
 * contactResource provide RESTful to map requests.
 * @author Suttanan Charoenpanich 5510547031
 *
 */
@Path("/contacts")
public class ContactResource {
	//DAO to manage with contact.
	private ContactDao dao = DaoFactory.getInstance().getContactDao(); 
	
	public ContactResource() {
		
	}
	
	/**
	 * to get all contact from dao.
	 * @return response OK 
	 */
	public Response getAllContacting() {
		List<Contact> allContact = dao.findAll(); 
		return Response.ok(new GenericEntity<List<Contact>>(allContact){}).build();
	}
	
	/**
	 * get the contact(s) by id
	 * @param id is id of contact
	 * @return response OK
	 * 		   response NOT FOUND if can't find the id 
	 */
	@GET
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML)
	public Response getContact( @PathParam("id") long id ) 
	{
		Contact contact = dao.find(id);
		if(contact == null){
			return Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND).build();
		}
		return Response.ok(contact).build();
	}
	
	/**
	 * manage by get all contact or get contact by title
	 * @param title is title of contact
	 * @return response OK
	 */
	@GET
	@Produces( MediaType.APPLICATION_XML)
	public Response getContact( @QueryParam("q") String title) {
		if( title == null ){
			return this.getAllContacting();
		}
		
		List<Contact> allContact = dao.findAll();
		List<Contact> newContact = new ArrayList<Contact>(); 
		for(int i = 0; i < allContact.size(); i++){
			if( allContact.get(i).getTitle().contains(title) ){
				newContact.add(allContact.get(i));
			}
		}
		return Response.ok(new GenericEntity<List<Contact>>(newContact){}).build();
	}
	
	/**
	 * to post contact in form xml.
	 * @param element is contact of this xml 
	 * @param uriInfo is uri of this contact.
	 * @return Response CREATED
	 */
	@POST
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response post( 
			JAXBElement<Contact> element, @Context UriInfo uriInfo ) {
		Contact contact = element.getValue();
		long newID = contact.getId();
		if( dao.find( newID ) != null){
			return Response.status(javax.ws.rs.core.Response.Status.CONFLICT).build();
		}
		dao.save( contact );
		URI uri = uriInfo.getAbsolutePathBuilder().path(contact.getId()+"").build();
		return Response.created(uri).build();
	}
	
	/**
	 * update contact by id.
	 * @param element is contact of this xml.
	 * @param uriInfo is uri of this xml.
	 * @param id is id of this contact
	 * @return response CREATED
	 */
	@PUT
	@Path("{id}")
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response put( 
		JAXBElement<Contact> element, @Context UriInfo uriInfo, @PathParam("id") long id ) {
		Contact contact = element.getValue();
		contact.setId(id);
		dao.update( contact );
		URI uri = uriInfo.getAbsolutePathBuilder().path(contact.getId()+"").build();
		return Response.created(uri).build();
	}
	
	/**
	 * delete contact by id
	 * @param id is id of contact
	 */
	@DELETE
	@Path("{id}")
	public void delete( @PathParam("id") long id ) {
		dao.delete(id); 
	}
	
}
	
