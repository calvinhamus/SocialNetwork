package course.cloud.computing.SocialNetwork;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import course.cloud.computing.classes.User;
import course.cloud.computing.classes.Users;
import cousre.cloud.computing.data.SocialNetworkDataBase;

@Path("/friendships")
public class FollowersAndFollingService 
{
	SocialNetworkDataBase db = SocialNetworkDataBase.getDatabase();
	
	@GET
	@Produces("application/xml")
	@Path("incoming")
	public Response getPendingFollowers(@Context HttpServletRequest req)
	{
		HttpSession session = req.getSession();
		Users users = new Users();
		int id = (int) session.getAttribute("userid");
		try {
			//Update to handle sessions
			users = db.getPendingFriend(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).entity(users).build();
	}
	@GET
	@Produces("application/xml")
	@Path("outgoing")
	public Response getPendingFollowing(@Context HttpServletRequest req)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		Users users = new Users();
		try {
			//Update to handle sessions
			users = db.getPendingFriend(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).entity(users).build();
	}
	@POST
	@Produces("application/xml")
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
				return Response.status(Status.FORBIDDEN).build();
			}
			user.setId( response);
			//user = db.checkFriendShip(id, friendId);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).entity(user).build();

	}
	@POST
	@Produces("application/xml")
	@Path("destroy/{friend-id}")
	public Response unfollowUser(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		User user = new User();
		try {
			//Update to handle sessions
			user.setId( db.unfollowUser(id, friendId));
			return Response.status(201).entity(user).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
	@GET
	@Produces("application/xml")
	@Path("friends/list")
	public Response getFollowing(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		Users users = new Users();
		try {
			//Update to handle sessions
			users = db.getFollowing(id);
			return Response.status(201).entity(users).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
	@GET
	@Produces("application/xml")
	@Path("followers/list")
	public Response getFollowers(@Context HttpServletRequest req,@PathParam("friend-id") int friendId)
	{
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("userid");
		Users users = new Users();
		try {
			//Update to handle sessions
			users = db.getFollowers(id);
			return Response.status(201).entity(users).build();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
}
