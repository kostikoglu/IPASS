package nl.hu.IPASS.ProjectIPASS.webservices;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
	
	@GET
	@Produces("application/json")
	public String getOnderdelen() {
		//System.out.println("getOnderdelen");
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
	
	@GET
	@Path("{naam}")
	@Produces("Application/json")
	public String getCountryInfo(@PathParam("naam") String naam) {
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
	
	@POST
	@Produces("application/json")
	public Response createCountry(@Context SecurityContext sc,
			@FormParam("naam") String naam,
			@FormParam("onderdeel_nr") int onderdeel_nr,
			@FormParam("prijs") double prijs, 
			@FormParam("beschrijving") String beschrijving) {
		
		OnderdeelService service = ServiceProvider.getOnderdeelService();
		
		Onderdeel newOnderdeel = service.saveOnderdeel(naam, onderdeel_nr, prijs, beschrijving);
		if (newOnderdeel == null) {
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("error", "Onderdeel does not exist!");
			return Response.status(409).entity(messages).build();
		}
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{naam}")
	@Produces("application/json")
	public Response deleteCountry(@Context SecurityContext sc, 
			@PathParam("naam") String naam) {
		
		OnderdeelService service = ServiceProvider.getOnderdeelService();

		if (service.deleteOnderdeel(naam)) {
			return Response.ok().build();
		}
		Map<String, String> messages = new HashMap<String, String>();
		messages.put("error", "Er is wat fout gegaan bij het deleten.");
		return Response.status(409).entity(messages).build();
	}
}