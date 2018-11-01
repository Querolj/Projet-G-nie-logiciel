package com.example.jetty_jersey.ws;

import DAO.DAO;
import DAO.Location;
import DAO.Map;
import DAO.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;

@Path("/location")
public class LocationRessource extends Ressource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAllByMap")
    public ArrayList<Location> getAllMapLocations(@Context HttpServletRequest httpRequest,
                                              @QueryParam("mapname") String mapName) {
        //todo security private map
        try {
            return DAO.getActionLocation().getLocations(DAO.client, mapName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public Location getLocation(@Context HttpServletRequest httpRequest,
                                @QueryParam("locationname") String locationName) {
        try {
            Location location = DAO.getActionLocation().getOneLocation(DAO.client, locationName);
            String mapname = location.getMapName();
            Map map = DAO.getActionMap().getOneMap(DAO.client, mapname);

            if (map.getIsPublic()) {
                return location;
            } else {
                try {
                    User user = getUserBySession(httpRequest);
                    if (user.getMaps().contains(mapname) || map.getPrivateUsers().contains(user.getUsername()))
                        return location;
                } catch (AuthException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
    public boolean addLocation(@Context HttpServletRequest httpRequest,
                               @FormParam("map") String map,
                               @FormParam("nameplace") String nameplace,
                               @FormParam("latitude") float latitude,
                               @FormParam("longitude") float longitude,
                               @FormParam("content") String content,
                               @FormParam("url") String url) {
        try {
            User user = getUserBySession(httpRequest);
            ArrayList<String> urls = new ArrayList<String>();
            urls.add(url);
            ArrayList<String> contents = new ArrayList<String>();
            contents.add(content);
            if (user.getMaps().contains(map)) {
                return DAO.getActionLocation().insertLocationAndUpdateMap(
                        DAO.client,
                        new Location(map, nameplace, latitude, longitude, urls, contents),
                        map);
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/remove")
    public boolean removeLocation(@Context HttpServletRequest httpRequest,
                                  @FormParam("mapname") String mapname,
                                  @FormParam("locationname") String locationName) {
        try {
            User user = getUserBySession(httpRequest);
            if (user.getMaps().contains(mapname)) {
                Map map = DAO.getActionMap().getOneMap(DAO.client, mapname);
                map.removeLocation(locationName);
                DAO.getActionLocation().deleteLocation(DAO.client, locationName);
                DAO.getActionMap().updateMap(DAO.client, map);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public boolean updateLocation(@Context HttpServletRequest httpRequest,
                                  @FormParam("mapname") String mapname,
                                  @FormParam("locationname") String locationName,
                                  @FormParam("description") String description,
                                  @FormParam("latitude") float latitude,
                                  @FormParam("longitude") float longitude) {
        try {
            User user = getUserBySession(httpRequest);
            if (user.getMaps().contains(mapname)) {
                Map map = DAO.getActionMap().getOneMap(DAO.client, mapname);
                if (map.getLocations().contains(locationName)) {
                    Location location = DAO.getActionLocation().getOneLocation(DAO.client, locationName);
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    // attention nom de la localisation non modifiable selon la BD localisationName = id
                    // pareil pour les map
                    location.content.add(description);
                    DAO.getActionLocation().updateLocation(DAO.client, location);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
