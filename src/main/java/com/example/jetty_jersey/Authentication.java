package com.example.jetty_jersey;

import DAO.DAO;
import DAO.User;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.elasticsearch.client.RestHighLevelClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class Authentication extends HttpServlet {
    private final static int PORT = 8080;
    private final static String ip = "localhost";
    private RestHighLevelClient client = DAO.ClientConnection(ip, PORT);

    @POST
    @Path("/login") // ({username}{password})
    public Response login(@PathParam("username") String username, @PathParam("password") String password) {
        try {
            User user = DAO.getActionUser().getOneUser(client, username);
            if (user != null) {
                Password pwd = new Password(password);
                if (user.getPassword() == pwd.toString()) {


                    // todo: add user to session
                    return Response.status(200).entity("user found").build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for username: " + username).build();
    }


    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

        context.addServlet(new ServletHolder(new DefaultServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                response.getWriter().append("hello " + request.getUserPrincipal().getName());
            }
        }), "/*");

        context.addServlet(new ServletHolder(new DefaultServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                response.getWriter().append("<html><form method='POST' action='/j_security_check'>"
                        + "<input type='text' name='j_username'/>"
                        + "<input type='password' name='j_password'/>"
                        + "<input type='submit' value='Login'/></form></html>");
            }
        }), "/login");

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);//__FORM_AUTH
        constraint.setRoles(new String[]{"user", "admin"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        UserStore userStore = new UserStore();
        userStore.addUser("username", new Password("password"), new String[]{"user"});

        HashLoginService loginService = new HashLoginService();
        loginService.setUserStore(userStore);

        FormAuthenticator authenticator = new FormAuthenticator("/login", "/login", false);

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.addConstraintMapping(constraintMapping);
        securityHandler.setLoginService(loginService);
        securityHandler.setAuthenticator(authenticator);

        context.setSecurityHandler(securityHandler);

        server.start();
        server.join();
    }
}
