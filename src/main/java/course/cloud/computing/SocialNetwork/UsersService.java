package course.cloud.computing.SocialNetwork;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cousre.cloud.computing.data.SocialNetworkDataBase;

@Path("/users")
public class UsersService implements IUsersService {

	SocialNetworkDataBase db = SocialNetworkDataBase.getDatabase();
	
	private int currentUser = 0;
	@POST
	@Produces("application/xml")
	@Path("create/{user-name}")
	public int createUser(@PathParam("user-name") String userName)
	{
		try {
			db.insertUser(userName, "temp@temp.com", "password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return 0;
	}

	@GET
	@Produces("application/xml")
	@Path("{users-name}")
	public Response getUserByName(@PathParam("users-name") String userName) 
	{
		try {
			int userId = db.getUserByName(userName);
			if(userId != 0)
				return Response.status(Status.OK).entity(userId).build();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@POST
	@Produces("application/xml")
	@Path("login/{user-id}")
	public Response loginUser(int userID) 
	{
		try {
			if(db.getUserById(userID) !=0){
				currentUser = userID;
				return Response.status(Status.ACCEPTED).build();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();

	}


}
