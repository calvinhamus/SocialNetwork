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

import course.cloud.computing.classes.User;
import course.cloud.computing.classes.Users;
import course.cloud.computing.data.SocialNetworkDataBase;

@Path("/friendships")
public class FollowersAndFollingService 
{
	SocialNetworkDataBase db = SocialNetworkDataBase.getDatabase();
	
	@GET
	@Produces("application/json")
	@Path("incoming")
	public Response getPendingFollowers(@Context HttpServletRequest req)
	{
		HttpSession session = req.getSession();
		Users users = new Users();
		int id = (int) session.getAttribute("userid");
		try {
			//Update to handle sessions
			users = db.getPendingFollow(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).header("Access-Control-Allow-Origin", "*").entity(users.toString()).build();
	}
	@GET
	@Produces("application/json")
	@Path("outgoing")
	public Response getPendingFollowing(@Context HttpServletRequest req)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		Users users = new Users();
		try {
			//Update to handle sessions
			users = db.getPendingFollowing(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).header("Access-Control-Allow-Origin", "*").entity(users.toString()).build();
	}
	@POST
	@Produces("application/json")
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("create/{friend-id}")
	public Response followUser(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		User user = new User();
		try {
			//Update to handle sessions
			int response =  db.followUser(id, friendId);
			if(response == 0){
				return Response.status(Status.FORBIDDEN).header("Access-Control-Allow-Origin", "*").build();
			}
			user.setId( response);
			//user = db.checkFriendShip(id, friendId);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).header("Access-Control-Allow-Origin", "*").entity(user.toString()).build();

	}
	@POST
	@Produces("application/json")
	@Path("destroy/{friend-id}")
	public Response unfollowUser(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		User user = new User();
		try {
			//Update to handle sessions
			user.setId( db.unfollowUser(id, friendId));
			return Response.status(201).header("Access-Control-Allow-Origin", "*").entity(user.toString()).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.FORBIDDEN).header("Access-Control-Allow-Origin", "*").build();
	}
	@GET
	@Produces("application/json")
	@Path("friends/list")
	public Response getFollowing(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		Users users = new Users();
		try {
			//Update to handle sessions
			users = db.getFollowing(id);
			return Response.status(201).header("Access-Control-Allow-Origin", "*").entity(users.toString()).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.FORBIDDEN).header("Access-Control-Allow-Origin", "*").build();
	}
	@GET
	@Produces("application/json")
	@Path("followers/list")
	public Response getFollowers(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		Users users = new Users();
		try {
			//Update to handle sessions
			users = db.getFollowers(id);
			return Response.status(201).header("Access-Control-Allow-Origin", "*").entity(users.toString()).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
}
