package DAO;

import java.io.IOException;
import java.util.ArrayList;

import org.elasticsearch.client.RestHighLevelClient;

public interface UserInterface {
	
	/*
	 * Create index users
	 * 
	 */
	void createIndexUser(RestHighLevelClient client) throws IOException;
	
    /**
     * 
     * @return the list of user
     * @throws IOException 
     */
    ArrayList<User> getUsers(RestHighLevelClient client) throws IOException;
    
    /**
     * 
     * @return the list of user following an interval
     * @throws IOException 
     */
    ArrayList<User> getUsers(RestHighLevelClient client, int from, int size) throws IOException;
    /**
     * 
     * @param username
     * @return all the user informations
     */
    User getOneUser(RestHighLevelClient client, String username) throws IOException;
    
    /**
     * @param username
     * @return the friendlist of the specific user
    */
    ArrayList<User> getFriends(RestHighLevelClient client, String username) throws IOException;
    
    /**
     * 
     * @param username
     * @return all the user maps
     */
    ArrayList<Map> getMapsOfUser(RestHighLevelClient client, String username) throws IOException;
    
    /**
     * 
     * @param user
     * @return false if already existing (or another error...), else true
     */
    boolean insertUser(RestHighLevelClient client, User user) throws IOException;
    
    /**
     * 
     * @param user
     * @return false if failed to update
     * @throws IOException 
     */
    boolean updateUser(RestHighLevelClient client, User user) throws IOException;
    
    /**
     * 
     * @param username, from, size
     * @return return a list of user corresponding to the username (not exact match) within the selected interval
     * @throws IOException 
     */
    ArrayList<User> searchUser(RestHighLevelClient client, String username, int from, int size) throws IOException;

}

