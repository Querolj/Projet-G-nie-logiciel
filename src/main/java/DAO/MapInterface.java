package DAO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.elasticsearch.client.RestHighLevelClient;

public interface MapInterface {
	
	/*
	 * Create index map
	 * 
	 */
	void createIndexMap(RestHighLevelClient client) throws IOException;
    /**
     * 
     * @return the list of all maps
     */
	ArrayList<Map> getMaps(RestHighLevelClient client) throws IOException;
    
	/**
     * 
     * @return the list of all maps following an interval 
     */
	ArrayList<Map> getMaps(RestHighLevelClient client, int from, int size) throws IOException;
	
    /**
     * @param user
     * @return the list of map of the user
     */
    Map getOneMap(RestHighLevelClient client, String name) throws IOException ;
    
    
    /**
     * 
     * @param date
     * @return the list of maps created after a specific date
     */
    ArrayList<Map> getMapsByTime(LocalDateTime date);
    
    /**
     * 
     * @return the list of public maps following an interval
     */
    ArrayList<Map> getPublicMaps(RestHighLevelClient client) throws IOException;
    /**
     * 
     * @return the list of public maps following an interval
     */
    ArrayList<Map> getPublicMaps(RestHighLevelClient client, int from, int size) throws IOException;

    /**
     * @param username
     * @return the list of public maps of the specific user following an interval
     */
    ArrayList<Map> getPublicMapsByUsername(RestHighLevelClient client, String username, int from, int size, boolean only_public, boolean only_private) throws IOException;
    
    /**
     * 
     * @param username
     * @return the list of maps of the specific user's friends, one list per friend
     */
    ArrayList<ArrayList<Map>> getFriendsMapByUsername(RestHighLevelClient client, String username, int from, int size) throws IOException;
    
    /**
     * create a new Map
     * @param mapname
     * @return true if the map was inserted, else false (already existing).
     */
    boolean insertMap(RestHighLevelClient client, Map map) throws IOException;
    
    
    /**
     * 
     * @param map_name
     * @return true if the request succeded, else false
     */
    boolean deleteMap(RestHighLevelClient client, String map_name) throws IOException;
    
    /**
     * 
     * @param map_name 
     * @return true if the request succeded, else false
     */
    boolean updateMap(RestHighLevelClient client, Map map) throws IOException;
    
    /**
     * 
     * @param name, from, size
     * @return return a list of maps corresponding to the name (not exact match) within the selected interval
     * @throws IOException 
     */
    ArrayList<Map> searchMap(RestHighLevelClient client, String name, int from, int size, boolean only_public, boolean only_private) throws IOException;
    
}
