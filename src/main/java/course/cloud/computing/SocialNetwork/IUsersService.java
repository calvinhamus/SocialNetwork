package course.cloud.computing.SocialNetwork;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public interface IUsersService 
{
	public int createUser(String userName);
	public Response getUserByName(String userName);
	public Response loginUser(HttpServletRequest req, int userID);
	
}
