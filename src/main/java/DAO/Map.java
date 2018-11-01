package DAO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;


public class Map {
    public String id =""; //C'est l'id : username_nomdemap"
    public String name;
    public String username;
    public ArrayList<String> locations;
    public ArrayList<String> privateUsers; //un username
    
    public boolean isPublic=true;
    public boolean isFavorite=false;
    
    /**
     * 
     * @param name 
     */
    public Map(String username,String name){
        this.id = username+"_"+name;
        this.username = username;
        this.name = name;
        locations = new ArrayList<String>();
        privateUsers = new ArrayList<String>();
    }
    
    /**
     * 
     */
    public Map(){
        locations = new ArrayList<String>();
        privateUsers = new ArrayList<String>();
    }
    
    /**
     * 
     * @param name 
     */
    public void setId(String name){
        this.id = name;
    }
    public String getId()
    {
    	return id;
    }
    
    public void setName(String nameToSearch){
        this.name = nameToSearch;
    }
    public String getName()
    {
    	return name;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername()
    {
    	return username;
    }
    
    /**
     * 
     */
    public void setIsFavorite(){
        isFavorite=true;
    }
    public boolean getIsFavorite()
    {
    	return isFavorite;
    }
    
    /**
     * 
     */
    public void setIsPublic(){
        isPublic=false;
    }
    public boolean getIsPublic()
    {
    	return isPublic;
    }
    
    /**
     * 
     */
    public void changeFavorite(){
        isFavorite=!isFavorite;
    }
    
    /**
     * 
     */
    public void changePublic(){
        isPublic=!isPublic;
    }
    
    /**
     * 
     * @param location 
     */
    public void addLocation(String idLocation){
        this.locations.add(idLocation);
    }
    
    /**
     * 
     * @param location 
     */
    public void removeLocation(String idLocation){
        this.locations.remove(idLocation);
    }
    public void setLocations(ArrayList<String> locations)
    {
    	this.locations = locations;
    }
    public ArrayList<String> getLocations()
    {
    	return this.locations;
    }

    public void setPrivateUsers(ArrayList<String> privateUsers)
    {
    	this.privateUsers = privateUsers;
    }
    public ArrayList<String> getPrivateUsers()
    {
    	return this.privateUsers;
    }
    
}













