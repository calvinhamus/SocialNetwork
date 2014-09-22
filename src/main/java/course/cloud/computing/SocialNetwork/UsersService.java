package course.cloud.computing.SocialNetwork;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.JSONP;

import course.cloud.computing.classes.User;
import cousre.cloud.computing.data.SocialNetworkDataBase;

@Path("/users")
public class UsersService //implements IUsersService 
{

	SocialNetworkDataBase db = SocialNetworkDataBase.getDatabase();
	@POST
	@Produces("application/xml")
	@Path("create/{user-name}")
	public Response createUser(@PathParam("user-name") String userName)
	{
		User newUser = new User();
		
		try {
			int newUserId = db.insertUser(userName, "temp@temp.com", "password");
			if(newUserId !=0){
				newUser.setId(newUserId);
				
				return Response.status(201).entity(newUser).build();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(Status.NOT_FOUND).build();
	}

	@GET
	@Produces("application/xml")
	//@Produces(MediaType.APPLICATION_JSON)
	@Path("{users-name}")
	public Response getUserByName(@PathParam("users-name") String userName) 
	//public Response getUserByName(String userName) 
	{
		User newUser = new User();
		try {
			int userId = db.getUserByName(userName);
			if(userId != 0){
				newUser.setId(userId);
				return Response.status(201).entity(newUser).build();
			}
				//return Response.status(Status.OK).entity(userId).build();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String json = //convert entity to json
			    return Response.status(Status.NOT_FOUND).build();
		//return Response.status(Status.NOT_FOUND).build();
	}

	@POST
	@JSONP
	@Produces("application/xml")
	//@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("login/{user-id}")
	public Response loginUser(@Context HttpServletRequest req,@PathParam("user-id") int userID) 
	{
		//@PathParam("users-name") int userName
		Integer id = null;
		try {
			if(db.getUserById(userID) !=0){
				//currentUser = userID;
				//return Response.status(Status.ACCEPTED).build();
				try {
						if (req == null) {
							System.out.println("Null request in context");
						}
						HttpSession session = req.getSession();
						id = (Integer) session.getAttribute("userid");
						if (id == null) {
							id = userID;
							session.setAttribute("userid", id);
							//return "Allocating a new id :" + id;
							return Response.status(Status.ACCEPTED).build();
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();

	}


}
