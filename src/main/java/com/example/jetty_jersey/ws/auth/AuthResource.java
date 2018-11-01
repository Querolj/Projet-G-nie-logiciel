package com.example.jetty_jersey.ws.auth;

import DAO.DAO;
import DAO.Map;
import DAO.User;
import com.example.jetty_jersey.ws.Ressource;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;

@PermitAll
@Path("/auth")
public class AuthResource extends Ressource{


    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@Context HttpServletRequest httpRequest) {
        return (User) httpRequest.getSession().getAttribute("user");
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponse login(@Context HttpServletRequest httpRequest,
                                @FormParam("username") String username,
                                @FormParam("password") String password) {
        
    	System.out.println("in /login");
        try {
			ArrayList<User> list = DAO.getActionUser().getUsers(DAO.client);
        for(User u: list)
        	System.out.println(u.toString());
        } catch (Exception e) {
        	e.printStackTrace();
        }
        User user = null;
		try {
            user = DAO.getActionUser().getOneUser(DAO.client, username);
		} catch (IOException ignore) {}
		if (user != null) {
            if (user.getPassword().equals(password)) {
                httpRequest.getSession().getId(); // initialize session
                httpRequest.getSession().setAttribute("user", user);
                
                return new SimpleResponse(true);
            }
            System.out.println("Mauvais mot de passe");
		}
        return new SimpleResponse(false);
    }


    @PUT
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public SimpleResponse signup(@Context HttpServletRequest httpRequest,
                                 @FormParam("username") String username,
                                 @FormParam("email") String email,
                                 @FormParam("password") String password,
                                 @FormParam("passwordConfirm") String passwordConfirm) {
        try {
            if (password.equals(passwordConfirm)) {
                User user = new User();
                user.setMail(email);
                user.setPassword(password);
                user.setUsername(username);

                if(DAO.getActionUser().insertUser(DAO.client, user))
				{
					Map map = new Map(username,"random");
					user.addMap(map.getId());
					Map map2 = new Map(username,"random2");
					user.addMap(map2.getId());

					DAO.getActionMap().insertMap(DAO.client, map);
					DAO.getActionMap().insertMap(DAO.client, map2);
					
					DAO.getActionUser().updateUser(DAO.client, user);
                	
                    return new SimpleResponse(true);
				}
				else
                    return new SimpleResponse(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SimpleResponse(false);
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponse logout(@Context HttpServletRequest httpRequest) {
        if (httpRequest.getSession().getAttribute("user") != null) {
            httpRequest.getSession().removeAttribute("user");
            return new SimpleResponse(true);
        }
        return new SimpleResponse(false);
    }

}
