package nl.hu.IPASS.ProjectIPASS.webservices;

import java.security.Key;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import nl.hu.IPASS.ProjectIPASS.persistence.UserDao;

@Path("/authentication")
public class AuthenticationResource {
	final static public Key key = MacProvider.generateKey();
	
	/*
	 * Authenticatie voor het inloggen
	 */

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
		
		username = username.toLowerCase(); // decapitalize username and password for login
		password = password.toLowerCase();
		try {
			// Authenticate the user against the database
			UserDao dao = new UserDao();
			String role = dao.findRoleForUser(username, password);
			
			if (role == null) {
				throw new IllegalArgumentException("No user found!");
			}
			
			String token = createToken(username, role);
			
			SimpleEntry<String, String> JWT = new SimpleEntry<String, String>("JWT", token);
			
			//System.out.print("[good] naam: " + username + " | " + "password: " + password + "\n");
			
			return Response.ok(JWT).build();
			
		} 
		catch (JwtException | IllegalArgumentException e) {
			//System.out.print("[wrong] naam: " + username + " | " + "password: " + password + "\n");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	/*
	 * Aanmaken JWT
	 */

	private String createToken(String username, String role) throws JwtException {
		Calendar expiration = Calendar.getInstance();
		expiration.add(Calendar.MINUTE, 30);
		
		return Jwts.builder()
				.setSubject(username)
				.setExpiration(expiration.getTime())
				.claim("role", role)
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();
	}
}