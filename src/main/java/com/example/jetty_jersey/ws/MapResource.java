package com.example.jetty_jersey.ws;

import DAO.DAO;
import DAO.Map;
import DAO.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

@Path("/map")
public class MapResource extends Ressource {

    /**
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public ArrayList<Map> getMaps() throws IOException {

        return DAO.getActionMap().getMaps(DAO.client);
    }


    /**
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/public")
    public ArrayList<Map> getPublicMaps() throws IOException {

        ArrayList<Map> list = DAO.getActionMap().getPublicMaps(DAO.client);
		if (list.size() == 0)
			return null;
		return list;
    }

    /**
     * @param mapname
     * @return
     * @throws IOException
     * @throws AuthException 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/by-name/{mapid}")
    public Map getOneMap(@Context HttpServletRequest httpRequest,@PathParam("mapid") String mapid) throws IOException, AuthException {
    	User user = getUserBySession(httpRequest);
        Map map = DAO.getActionMap().getOneMap(DAO.client, mapid);
        if(map==null)
        	return null;
        if(map.getIsPublic() || map.getUsername().equals(user.getUsername()))
        	return map;
        else
        	return null;
    }

    /**
     * @param mapname
     * @return
     * @throws Exception
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/by-name/{mapid}")
    public Response deleteMap(@Context HttpServletRequest httpRequest,@PathParam("mapid") String mapid) throws Exception {
        User user = getUserBySession(httpRequest);
        Map map = DAO.getActionMap().getOneMap(DAO.client, mapid);
        boolean success = false;
        if(map==null)
        	return Response.status(Response.Status.NOT_MODIFIED).build();
        if(map.getUsername().equals(user.getUsername()))
        	success = DAO.getActionMap().deleteMap(DAO.client, mapid);
        return (success) ? Response.status(Response.Status.ACCEPTED).build() : Response.status(Response.Status.NOT_MODIFIED).build();
    }

    /**
     * @param mapname
     * @return
     * @throws IOException
     * @throws AuthException 
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/by-name/{mapname}")
    public Map modifMap(@Context HttpServletRequest httpRequest,@PathParam("mapname") String mapname) throws IOException, AuthException {
        User user = getUserBySession(httpRequest);
        Map map = DAO.getActionMap().getOneMap(DAO.client, mapname);
        boolean success = false;
        if(map.getUsername().equals(user.getUsername()))
        	success = DAO.getActionMap().updateMap(DAO.client, map);
        return (success) ? DAO.getActionMap().getOneMap(DAO.client, mapname) : map;
    }

    /**
     *
     * @param tokken
     * @return
     * @throws IOException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/public")
    public ArrayList<Map> searchMap(@FormParam("tokken") String tokken) throws IOException {

        return DAO.getActionMap().searchMap(DAO.client, tokken, 0, 10,true,false);
    }

    /**
     * @param mapname
     * @return
     * @throws IOException
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
    public Map createMap(@Context HttpServletRequest httpRequest,
    					@FormParam("mapname") String mapname,
    					@FormParam("public") String isPublic) throws IOException {
        try {
            User user = getUserBySession(httpRequest);
            Map map = new Map(user.getUsername(),mapname);
            if(isPublic==null)
            	map.changePublic();
            user.addMap(map.getId());

            DAO.getActionMap().insertMap(DAO.client, map);
            DAO.getActionUser().updateUser(DAO.client, user);
            return map;
        } catch (AuthException e) {
            e.printStackTrace();
        }
        return null;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/share")
    public boolean shareMap(@Context HttpServletRequest httpRequest, @FormParam("mapname") String mapname, @FormParam("friendname") String friendname) throws IOException {
        try {
            User user = getUserBySession(httpRequest);
            if(!user.getFriends().contains(friendname))
            	return false;
            Map map = DAO.getActionMap().getOneMap(DAO.client, mapname);
            if(map==null)
            	return false;
            if (user.getMaps().contains(map.getId())) {
                User otherUser = DAO.getActionUser().getOneUser(DAO.client, friendname);
                map.privateUsers.add(otherUser.getUsername());
                otherUser.addMap(mapname);
                DAO.getActionUser().updateUser(DAO.client, otherUser);
                DAO.getActionMap().updateMap(DAO.client, map);
                return true;
            }
        } catch (AuthException e) {
            e.printStackTrace();
        }
        return false;
    }


}
