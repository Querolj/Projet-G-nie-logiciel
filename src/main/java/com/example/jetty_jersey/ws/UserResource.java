package com.example.jetty_jersey.ws;

import DAO.DAO;
import DAO.Map;
import DAO.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;

@Path("/user")
public class UserResource extends Ressource {

    /**
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public ArrayList<User> getListUser() throws IOException {

        return DAO.getActionUser().getUsers(DAO.client);
    }

    /**
     * @return
     * @throws IOException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public ArrayList<User> searchUser(@FormParam("utokken") String tokken) throws IOException {

        return DAO.getActionUser().searchUser(DAO.client, tokken, 0, 10);
    }
    
    /**
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/maps")
    public ArrayList<Map> getMapsOfUser(@Context HttpServletRequest httpRequest) throws Exception {
		System.out.println("/maps");
    	User user = getUserBySession(httpRequest);
		System.out.println(user.toString());
		
        ArrayList<Map> list = DAO.getActionUser().getMapsOfUser(DAO.client, user.getUsername());
		if (list.size() == 0)
			return null;
		return list;
    }

    /**
     * @param username
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/by-name/{username}")
    public ArrayList<Map> getUserMaps(@PathParam("username") String username) throws IOException {
    	ArrayList<Map> list = DAO.getActionMap().getPublicMapsByUsername(DAO.client, username,0,10,true,true);
    	return list;
    }
    
    

    /**
     * @param username
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/by-name/{username}/friends")
    public ArrayList<User> getFriends(@PathParam("username") String username) throws IOException {
    	ArrayList<User> list = DAO.getActionUser().getFriends(DAO.client, username);
        return list;
    }

    /**
     * @param username
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/by-name/{username}/info")
    public User getUserBySession(@PathParam("username") String username) throws IOException {

        return DAO.getActionUser().getOneUser(DAO.client, username);
    }

    /**
     * @param username
     * @return
     * @throws Exception
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/info")
    public boolean modifyUserData(@Context HttpServletRequest httpRequest,
    							@FormParam("email") String email,
    							@FormParam("password") String password,
    							@FormParam("passwordConfirm") String passwordConfirm) throws IOException, AuthException {
        User user = getUserBySession(httpRequest);
        if(!password.equals(passwordConfirm))
        	return false;
        user.setMail(email);
        user.setPassword(password);
        return DAO.getActionUser().updateUser(DAO.client, user);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/friends")
    public ArrayList<String> getFriends(@Context HttpServletRequest httpRequest) throws Exception {
        User user = getUserBySession(httpRequest);
        return user.getFriends();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/friends/add")
    public boolean addFriend(@Context HttpServletRequest httpRequest, @FormParam("friendname") String friendName) throws Exception {
        User user = getUserBySession(httpRequest);
        User friend = DAO.getActionUser().getOneUser(DAO.client, friendName);
        if (friend != null) {
            user.getFriends().add(friend.getUsername());
            friend.getFriends().add(user.getUsername());
            DAO.getActionUser().updateUser(DAO.client, user);
            DAO.getActionUser().updateUser(DAO.client, friend);
            return true;
        }
        return false;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/friends/remove")
    public boolean removeFriend(@Context HttpServletRequest httpRequest, @FormParam("friendname") final String friendName) throws Exception {
        User user = getUserBySession(httpRequest);
        if (user.friends.contains(friendName)) {
        	User friend = DAO.getActionUser().getOneUser(DAO.client,friendName);
            friend.getFriends().remove(user.getUsername());
        	user.getFriends().remove(friendName);
            DAO.getActionUser().updateUser(DAO.client, user);
            DAO.getActionUser().updateUser(DAO.client, friend);

            return true;
        }
        return false;
    }
}
