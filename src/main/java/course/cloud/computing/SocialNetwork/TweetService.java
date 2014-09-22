package course.cloud.computing.SocialNetwork;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import course.cloud.computing.classes.Tweet;
import course.cloud.computing.classes.Users;
import cousre.cloud.computing.data.SocialNetworkDataBase;

@Path("/tweet")
public class TweetService 
{
SocialNetworkDataBase db = SocialNetworkDataBase.getDatabase();
	
	@POST
	@Produces("application/xml")
	@Path("tweet/{msg}")
	public Response sendTweet(@Context HttpServletRequest req,@PathParam("msg") String msg)
	{
		if(msg.length() > 128)
			return Response.status(Status.FORBIDDEN).build();//TODO FINSIH this
		HttpSession session = req.getSession();
		Tweet tweet = new Tweet();
		int id = (int) session.getAttribute("userid");
		try {
			//Update to handle sessions
			 int tweetId = db.createNewTweet(id, msg);
			 if(tweetId !=0){
				 tweet.setId(tweetId);
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).entity(tweet).build();
	}
	@GET
	@Produces("application/xml")
	@Path("show/{id}")
	public Response getTweetById(@Context HttpServletRequest req,@PathParam("id") int tweetId)
	{
		try {
			//Update to handle sessions
			Tweet tweet = db.getTweetById(tweetId);
			return Response.status(201).entity(tweet).build();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
	@POST
	@Produces("application/xml")
	@Path("destroy/{id}")
	public Response deleteTweetById(@Context HttpServletRequest req,@PathParam("id") int tweetId)
	{
		HttpSession session = req.getSession();
		Users users = new Users();
		int id = (int) session.getAttribute("userid");
		try {
			//Update to handle sessions
			if(db.removeTweet(id, tweetId) !=0);
				return Response.status(201).entity("Destroyed").build();//TODO add success parameters
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).entity(users).build();
	}
	@POST
	@Produces("application/xml")
	@Path("retweet/{id}")
	public Response retweetTweet(@Context HttpServletRequest req,@PathParam("id") int tweetId)
	{
		HttpSession session = req.getSession();
		Tweet tweet = new Tweet();
		int id = (int) session.getAttribute("userid");
		try {
			//Update to handle sessions
			 tweet = db.getTweetById(tweetId);
			 String originalMsg = tweet.getMessage();
			 tweet.setMessage("UserID: "+id+" Tweet: "+ originalMsg);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return Response.status(201).entity(tweet).build();
	}
}
