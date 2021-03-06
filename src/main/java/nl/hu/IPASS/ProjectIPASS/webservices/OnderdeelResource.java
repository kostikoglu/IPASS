package nl.hu.IPASS.ProjectIPASS.webservices;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.hu.IPASS.ProjectIPASS.webservices.Onderdeel;
import nl.hu.IPASS.ProjectIPASS.webservices.ServiceProvider;
import nl.hu.IPASS.ProjectIPASS.webservices.OnderdeelService;

import javax.ws.rs.core.Response;

@Path("/onderdelen")
public class OnderdeelResource {
	
	/*
	 * GET request voor het ophalen van de onderdelen
	 */
	
	@GET
	@Produces("application/json")
	public String getOnderdelen() {
		OnderdeelService service = ServiceProvider.getOnderdeelService();
		JsonArrayBuilder jab = Json.createArrayBuilder();

		for (Onderdeel onderdeel : service.getAllOnderdelen()) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("naam", onderdeel.getNaam());
			job.add("onderdeel_nr", onderdeel.getOnderdeel_nr());
			job.add("prijs", onderdeel.getPrijs());
			job.add("beschrijving", onderdeel.getBeschrijving());

			jab.add(job);
		}
		JsonArray array = jab.build();
		return array.toString();
	}
	
	/*
	 * GET request voor het ophalen van een specifiek onderdeel
	 */
	
	@GET
	@Path("{naam}")
	@Produces("Application/json")
	public String getOnderdeelInfo(@PathParam("naam") String naam) {
		OnderdeelService service = ServiceProvider.getOnderdeelService();
		Onderdeel onderdeel = service.find(naam);

		if (onderdeel == null) {
			throw new WebApplicationException("No such order!");
		}

		JsonArrayBuilder jab = Json.createArrayBuilder();

		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("naam", onderdeel.getNaam());
		job.add("onderdeel_nr", onderdeel.getOnderdeel_nr());
		job.add("prijs", onderdeel.getPrijs());
		job.add("beschrijving", onderdeel.getBeschrijving());

		jab.add(job);

		JsonArray array = jab.build();
		return array.toString();
	}
	
	/*
	 * PUT request voor het wijzigen van de gegevens
	 */
	
	@PUT
	@Path("{naam}")
	@Produces("application/json")
	public Response updateOnderdeel(@Context SecurityContext sc,
			@FormParam("naam") String naam,
			@FormParam("prijs") Double prijs, 
			@FormParam("beschrijving") String beschrijving) throws SQLException {
		
		if(naam.length() <= 0 && prijs != null && beschrijving.length() <= 0) { // als de velden niet/incorrect zijn ingevuld gaat de opdracht niet door
			return Response.status(403).build();
		}
		
		OnderdeelService service = ServiceProvider.getOnderdeelService();
		
		Onderdeel onderdeel = service.updateOnderdeel(naam, prijs, beschrijving);

		if (onderdeel == null) {
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("error", "Onderdeel does not exist!");
			return Response.status(409).entity(messages).build();
		}
		return Response.ok(onderdeel).build();
	}
	
	/*
	 * POST request voor het invoeren van gegevens
	 */
	
	@POST
	@Produces("application/json")
	public Response createOnderdeel(@Context SecurityContext sc,
			@FormParam("naam") String naam,
			@FormParam("prijs") Double prijs, 
			@FormParam("beschrijving") String beschrijving) {
		
		if(naam.length() <= 0 && prijs != null && beschrijving.length() <= 0) { // als de velden niet/incorrect zijn ingevuld gaat de opdracht niet door
			return Response.status(403).build();
		}
		
		OnderdeelService service = ServiceProvider.getOnderdeelService();
		
		Onderdeel newOnderdeel = service.saveOnderdeel(naam, prijs, beschrijving);
		if (newOnderdeel == null) {
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("error", "Onderdeel does not exist!");
			return Response.status(409).entity(messages).build();
		}
		return Response.ok().build();
	}
	
	/*
	 * DELETE request voor het verwijderen van de gegevens
	 */
	
	@DELETE
	@Path("{naam}")
	@Produces("application/json")
	public Response deleteOnderdeel(@Context SecurityContext sc, @PathParam("naam") String naam) {
		
		OnderdeelService service = ServiceProvider.getOnderdeelService();

		service.deleteOnderdeel(naam);
		
		return Response.ok().build();
	}
}
