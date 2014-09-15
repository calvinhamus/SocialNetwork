package course.cloud.computing.SocialNetwork;

import javax.ws.rs.core.Response;

public interface IUsersService 
{
	public int createUser(String userName);
	public Response getUserByName(String userName);
	public Response loginUser(int userID);
	
}
