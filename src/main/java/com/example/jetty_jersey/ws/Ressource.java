package com.example.jetty_jersey.ws;

import DAO.DAO;
import DAO.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class Ressource {
    class AuthException extends Exception {
        public AuthException(String msg) {
            super(msg);
        }
    }

    public User getUserBySession(HttpServletRequest httpRequest) throws AuthException {
        User user = (User) httpRequest.getSession().getAttribute("user");
        if (user == null)
            throw new AuthException("user not in session");

        try {
            return DAO.getActionUser().getOneUser(DAO.client, user.getUsername());
        } catch (IOException e) {
            throw new AuthException("user no longer exists");
        }
    }
}
