package course.cloud.computing.classes;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement 
public class Users 
{
	private ArrayList<User> userList= new ArrayList<User>();

	public ArrayList<User> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	} 
	public void addUser(User user)
	{
		userList.add(user);
	}
}
