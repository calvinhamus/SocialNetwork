package cousre.cloud.computing.data;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import course.cloud.computing.classes.Tweet;
import course.cloud.computing.classes.User;
import course.cloud.computing.classes.Users;


public class SocialNetworkDataBase 
{
	private static AtomicBoolean init = new AtomicBoolean(false);
	private static Connection conn = null;
	//private PreparedStatement statement = null;
	//private PreparedStatement lookup;

	private Set<String> getDBTables(Connection targetDBConn)
			throws SQLException 
	{
		Set<String> set = new HashSet<String>();
		DatabaseMetaData dbmeta = targetDBConn.getMetaData();
		readDBTable(set, dbmeta, "TABLE", null);
		return set;
	}

	private void readDBTable(Set<String> set, DatabaseMetaData dbmeta,
			String searchCriteria, String schema) throws SQLException 
	{
		ResultSet rs = dbmeta.getTables(null, schema, null,
				new String[] { searchCriteria });
		while (rs.next()) {
			set.add(rs.getString("TABLE_NAME").toLowerCase());
		}
	}

	public static SocialNetworkDataBase getDatabase() 
	{
		SocialNetworkDataBase socialNetworkdb = new SocialNetworkDataBase();
		try {
			socialNetworkdb.initialize();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return socialNetworkdb;
	}
	private void initialize() throws InstantiationException,
		IllegalAccessException, ClassNotFoundException, SQLException 
	{
		boolean setDB = init.compareAndSet(false, true);
		if (setDB) {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Class.forName(driver).newInstance();
			String protocol = "jdbc:derby:";
			Properties props = new Properties();
			conn = DriverManager.getConnection(
					protocol + "socialNetowrkDB;create=true", props);
			if (conn != null) {
				System.out.println("Created the connection");
			}
			if (!getDBTables(conn).contains("users")) 
					generateUserTable();
			if (!getDBTables(conn).contains("pending"))
					generatePendingTable();
			if (!getDBTables(conn).contains("userfollowers"))
					generateUserFollowersTable();
			if (!getDBTables(conn).contains("userfollowing"))
					generateUserFollowingTable();
			if (!getDBTables(conn).contains("tweets"))
					generateTweetTable();
			//if (!getDBTables(conn).contains("usertweets"))
					//generateUserTweetsTable();
			if (!getDBTables(conn).contains("userfriends"))
					generateFriendsTable();
		}
	}
	/****************************************************************User Section******************************************/
	public int insertUser(String userName,String email, String password) throws SQLException {
		PreparedStatement statement = null;
		if(getUserByName(userName) == 0)
		{
			if (statement == null) {
				statement = conn
						.prepareStatement("insert into users(userName,email,password) values (?, ?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
			}
			statement.setString(1, userName);
			statement.setString(2, email);
			statement.setString(3, password);
			statement.execute();
			ResultSet set = statement.getGeneratedKeys();
			if (set.next()){ 
				return set.getInt(1);
			}
			    
		}
		return 0;
		
	}
	public int getUserById(int Id) throws SQLException
	{
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("select id from users where id = ?");
		}
		lookup.setInt(1, Id);
		ResultSet set = lookup.executeQuery();
		int result = 0;
		if (set.next()) {
			result = set.getInt(1);
		}
		return result;
	}
	public int getUserByName(String name) throws SQLException
	{
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("select id from users where username = ?");
		}
		lookup.setString(1, name);
		ResultSet set = lookup.executeQuery();
		int result = 0;
		if (set.next()) {
			result = set.getInt(1);
		}
		return result;
	}
	public long getUsers() throws SQLException {
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("select count(*) from users ");
		}
		//lookup.setString(1, URL);
		ResultSet set = lookup.executeQuery();
		long count = 0;
		if (set.next()) {
			count = set.getLong(1);
		}
		return count;
	}
	/**************************************************************** End User Section******************************************/
	/****************************************************  Followers and Following Section******************************************/
	public Users getPendingFriend(int userId) throws SQLException
	{
		Users users = new Users();
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT userId FROM pending WHERE friendId = ?");
		}
		lookup.setInt(1, userId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			User tempUser = new User();
			tempUser.setId(set.getInt(1));
			users.addUser(tempUser);
		}
		return users;
	}
	public Users getPendingFollow(int userId) throws SQLException
	{
		Users users = new Users();
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT userId FROM pending WHERE friendId  = ?");
		}
		lookup.setInt(1, userId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			User tempUser = new User();
			tempUser.setId(set.getInt(1));
			users.addUser(tempUser);
		}
		return users;
	}
	public User checkFriendShip(int userId,int friendId) throws SQLException
	{
		User user = new User();
		if(checkCurrentRelationship(userId, friendId) !=403)
		{
			if(checkPendingRelationship(userId, friendId) == 1 )
			{
				createFriendship(userId,friendId);
				user.setMessage("Success");
			}else
			{
				if(checkIfPendingExists(userId,friendId) == 0){
					createPending(userId,friendId);		
				}
				user.setMessage("User Request Pending");
			}
		}else
		{
			user.setMessage("HTTP403");
		}
		return user;
	}
	private int checkIfPendingExists(int userId, int friendId) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT id FROM pending WHERE userId = ? AND friendId  = ?");
		}
		lookup.setInt(1, userId);
		lookup.setInt(2, friendId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
				return 1;
		}
		return 0;
	}

	private int createPending(int userId, int friendId) throws SQLException 
	{
		PreparedStatement statement = null;
		if (statement == null) {
			statement = conn
					.prepareStatement("INSERT INTO pending(userId,friendId) VALUES(?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);
		}
		statement.setInt(1, userId);
		statement.setInt(2, friendId);
		statement.execute();
		ResultSet set = statement.getGeneratedKeys();
		if (set.next()){ 
			return set.getInt(1);
		}
		return 0;
	}

	private int createFriendship(int userId, int friendId) throws SQLException 
	{
		
		PreparedStatement statement = null;
			if (statement == null) {
				statement = conn
						.prepareStatement("INSERT INTO userFriends(userId,friendId) VALUES(?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);
			}
			statement.setInt(1, userId);
			statement.setInt(2, friendId);
			statement.execute();
			ResultSet set = statement.getGeneratedKeys();
			if (set.next()){ 
				return set.getInt(1);
			}
			return 0;
		//return user;
	}
	public int followUser(int userId, int followingId) throws SQLException 
	{
		if(!checkFollowingStatus(userId,followingId))
		{
			PreparedStatement statement = null;
			if (statement == null) {
				statement = conn
						.prepareStatement("INSERT INTO userFollowing(userId,followeringId) VALUES(?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);
			}
			statement.setInt(1, userId);
			statement.setInt(2, followingId);
			statement.execute();
			ResultSet set = statement.getGeneratedKeys();
			if (set.next()){ 
				return followingId;
			}			
		}
			return 0;
		
		//return user;
	}
	private boolean checkFollowingStatus(int userId, int followingId) throws SQLException {
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT followeringId FROM userfollowing WHERE userId = ?");
		}
		lookup.setInt(1, userId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			return true;
		}
		return false;
	}

	public int unfollowUser(int userId, int followingId) throws SQLException
	{
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("DELETE FROM userfollowing WHERE userId = ? AND followeringId = ?");
		}
		lookup.setInt(1, userId);
		lookup.setInt(2, followingId);
		if(lookup.executeUpdate()>0)
		{
			return followingId;
		}
			
		//lookup.executeQuery();		
		return 0;
	}
	public Users getFollowing(int userId) throws SQLException
	{
		Users users = new Users();
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT followeringId FROM userfollowing WHERE userId = ?");
		}
		lookup.setInt(1, userId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			User tempUser = new User();
			tempUser.setId(set.getInt(1));
			users.addUser(tempUser);
		}
		return users;
	}
	public Users getFollowers(int userId) throws SQLException
	{
		Users users = new Users();
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT followerId FROM userfollowers WHERE userId = ?");
		}
		lookup.setInt(1, userId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			User tempUser = new User();
			tempUser.setId(set.getInt(1));
			users.addUser(tempUser);
		}
		return users;
	}
	/**********Tweet funcitons ************/
	public int createNewTweet(int userId, String msg) throws SQLException
	{
		PreparedStatement statement = null;
		if (statement == null) {
			statement = conn
					.prepareStatement("INSERT INTO tweets(userId,text) VALUES(?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);
		}
		statement.setInt(1, userId);
		statement.setString(2, msg);
		statement.execute();
		ResultSet set = statement.getGeneratedKeys();
		if (set.next()){
			
			return set.getInt(1);
		}
		return 0;
	}
	public Tweet getTweetById(int tweetId) throws SQLException
	{
		Tweet tweet = new Tweet();
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT * FROM tweets WHERE id = ?");
		}
		lookup.setInt(1, tweetId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			
			tweet.setMessage(set.getString("text"));
			tweet.setId(set.getInt("id"));
				return tweet;
		}
		return tweet;
	}
	public int removeTweet(int userId, int tweetId) throws SQLException
	{
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("DELETE FROM tweets WHERE userId = ? AND id = ?");
		}
		lookup.setInt(1, userId);
		lookup.setInt(2, tweetId);
		if(lookup.executeUpdate() >0)
			return 1;
		//lookup.executeQuery();		
		return 0;
	}
	/**********End Tweet funcitons************/
	/*************************Helper functions ***************************************************************/
	private int checkPendingRelationship(int userId,int friendId) throws SQLException
	{
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT userId FROM pending WHERE friendId  = ?");
		}
		lookup.setInt(1, userId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			if(friendId == set.getInt(1))
				return 1;
		}
		return 0;
	}
	private int checkCurrentRelationship(int userId, int friendId) throws SQLException
	{
		PreparedStatement lookup = null;
		if (lookup == null) {
			lookup = conn
					.prepareStatement("SELECT id FROM userfollowers WHERE userId  = ? "
							+ "and friendId = ? ");
		}
		lookup.setInt(1, userId);
		lookup.setInt(2, friendId);
		ResultSet set = lookup.executeQuery();
		if (set.next()) {
			return 403;
		}
		return 0;
	}
	/************************* End Helper functions ***************************************************************/
	/*********************************************************End Followers and Following Section******************************************/
	private void generateUserTable() throws SQLException 
	{
		//User Table
		boolean results = conn
				.createStatement()
				.execute(
						"Create table users "
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) " 
						+ ", userName VARCHAR(255)"
						+ ", email VARCHAR(255)"
						+ ", password VARCHAR(255)"
						+ ",PRIMARY KEY (id))"
						);

		if (results) {
			System.out
					.println("User Tables were created");
		}
		
	}
	/******V2********/
	private void generateUserTweetsTable() throws SQLException {
		boolean results = conn
				.createStatement()
				.execute(
						
						"Create table userTweets"
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" //User Tweets table
						+ ", userId INT"
						+ ", tweetId INT"
						+ ", PRIMARY KEY (id)"
						+ ", FOREIGN KEY (userId) REFERENCES users"
						+ ", FOREIGN KEY (tweetId) REFERENCES tweets)");

		if (results) {
			System.out
					.println("UserTweets Tables were created");
		}
		
	}
/*********V2***********/
	private void generateTweetTable() throws SQLException {
		boolean results = conn
				.createStatement()
				.execute(
						"Create table tweets"
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)"
						+ ",userId INT" //Tweets Tab
						+ ", text VARCHAR(128)"
						+ ", date TIMESTAMP"
						+ ",PRIMARY KEY (id)"
						+ ", FOREIGN KEY (userId) REFERENCES users)");

		if (results) {
			System.out
					.println("Tweet Tables were created");
		}
	}

	private void generateUserFollowingTable() throws SQLException {
		boolean results = conn
				.createStatement()
				.execute(
						 "Create table userFollowing "
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" //User Following Table
						+ ", userId INT"
						+ ", followeringId INT"
						+ ", PRIMARY KEY (id)"
						+ ", FOREIGN KEY (userId) REFERENCES users"
						+ ", FOREIGN KEY (followeringId) REFERENCES users)");

		if (results) {
			System.out
					.println("User Following Tables were created");
		}
	}

	private void generateUserFollowersTable() throws SQLException {
		boolean results = conn
				.createStatement()
				.execute(
						"Create table userFollowers "
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" //User Follower table
						+ ", userId INT"
						+ ", followerId INT"
						+ ", PRIMARY KEY (id)"
						+ ", FOREIGN KEY (userId) REFERENCES users"
						+ ", FOREIGN KEY (followerId) REFERENCES users)");

		if (results) {
			System.out
					.println("UserFollowers Tables were created");
		}
	}
	private void generatePendingTable() throws SQLException {
		boolean results = conn
				.createStatement()
				.execute(
						 " Create table pending "
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" //Pending Table
						+ ", userId INT"
						+ ", friendId INT"
						+ ", PRIMARY KEY (id)"
						+ ", FOREIGN KEY (userId) REFERENCES users"
						+ ", FOREIGN KEY (friendId) REFERENCES users)");

		if (results) {
			System.out
					.println("Pending Table were created");
		}
	}
	private void generateFriendsTable() throws SQLException {
		boolean results = conn
				.createStatement()
				.execute(
						 " Create table userFriends "
						+ "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" //Pending Table
						+ ", userId INT"
						+ ", friendId INT"
						+ ", PRIMARY KEY (id)"
						+ ", FOREIGN KEY (userId) REFERENCES users"
						+ ", FOREIGN KEY (friendId) REFERENCES users)");

		if (results) {
			System.out
					.println("Pending Table were created");
		}
	}

	

}
