package course.cloud.computing.SocialNetwork;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import cousre.cloud.computing.data.SocialNetworkDataBase;

public class App 
{
	public static final String BASE_URI = "http://localhost:9090/plugin/";
	
	 public static void main(String[] args) throws IOException
	 {
	        final HttpServer server = startServer();
	        System.out.println(String.format("Jersey app started with WADL available at "
	                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
	        System.in.read();
	        server.stop();
	    
	 }
	 public static HttpServer startServer()
	 {
 		
 		 SocialNetworkDataBase database = SocialNetworkDataBase.getDatabase();
 	
	     // create a resource config that scans for JAX-RS resources and providers
	     // in com.example package
	     final ResourceConfig rc = new ResourceConfig().packages("course.cloud.computing.SocialNetwork");
	
	     // create and start a new instance of grizzly http server
	     // exposing the Jersey application at BASE_URI
	     try {
			testService(database);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	     return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);   
	 }
	 private static void testService(SocialNetworkDataBase db) throws SQLException
	 {
		 //int success = db.getUserByName("Calvin02");
		 //int success = db.getUserById(10);
		// long success = db.getUsers();
		 int success =  db.insertUser("Calvin9", "a@a.com", "password");
		 System.out.print("Insert success" + success);
	 }
}
