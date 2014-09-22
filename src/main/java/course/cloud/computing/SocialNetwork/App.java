package course.cloud.computing.SocialNetwork;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import cousre.cloud.computing.data.SocialNetworkDataBase;

public class App 
{
	//HttpServer server;
	public static final String BASE_URI = "http://localhost:9090/";
	
	public static void main(String[] args) throws IOException {
		final SelectorThread server = startServer();
		System.out.println(String.format(
				"Jersey app started with WADL available at "
						+ "%sapplication.wadl\nHit enter to stop it...",
				BASE_URI));
		System.in.read();
		server.stopEndpoint();
	}

	private static SelectorThread startServer()
			throws IllegalArgumentException, IOException {
		
		SocialNetworkDataBase database = SocialNetworkDataBase.getDatabase();
		// TODO Auto-generated method stub
		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("com.sun.jersey.config.property.packages",
				"course.cloud.computing.SocialNetwork");
		SelectorThread factory = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
		return factory;
	}
	
//	 public static void main(String[] args) throws IOException
//	 {
//	        final HttpServer server = startServer();
//	        System.out.println(String.format("Jersey app started with WADL available at Social Network "
//	                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
//	        System.in.read();
//	        server.stop();
//	    
//	 }
//	 public static HttpServer startServer()
//	 {
// 		
// 		 SocialNetworkDataBase database = SocialNetworkDataBase.getDatabase();
// 	
//	     // create a resource config that scans for JAX-RS resources and providers
//	     // in com.example package
//	     final ResourceConfig rc = new ResourceConfig().packages("course.cloud.computing.SocialNetwork");//.
//	               // register(createMoxyJsonResolver());
//	
//	     // create and start a new instance of grizzly http server
//	     // exposing the Jersey application at BASE_URI
//	     
//	     return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);   
//	 }
	 private static void testService(SocialNetworkDataBase db) throws SQLException
	 {
		 //int success = db.getUserByName("Calvin02");
		 //int success = db.getUserById(10);
		// long success = db.getUsers();
		 int success =  db.insertUser("Calvin9", "a@a.com", "password");
		 System.out.print("Insert success" + success);
	 }
}
