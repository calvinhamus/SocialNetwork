package course.cloud.computing.classes;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement 
public class Tweet 
{
	int id;
	String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", message=" + message +"]";
	}
}
