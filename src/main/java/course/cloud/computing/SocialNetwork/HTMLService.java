package course.cloud.computing.SocialNetwork;

	import javax.ws.rs.GET;
	import javax.ws.rs.Path;
	import javax.ws.rs.Produces;
	import javax.ws.rs.core.MediaType;

	@Path("/button")
	public class HTMLService {
		
		@GET
		@Produces(MediaType.TEXT_HTML)
		@Path("login")
		public String getDivContents(){
			return "<button type='button' name='tracker-button' onclick='reportClick()'> Boo! </button>"
					+ "<button type='button' name='tracker-button' onclick='reportClick()'> Dislike! </button>";
		}

	}


