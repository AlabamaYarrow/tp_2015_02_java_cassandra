package main;

import freemarker.template.Configuration;
import frontend.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import templater.PageGenerator;

import javax.servlet.Servlet;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8100;
        if (args.length == 1) {
            String portString = args[0];
            port = Integer.valueOf(portString);
        }
        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        AccountService accountService = new AccountService();

        Servlet admin = new AdminServlet(accountService, new PageGenerator("templates", new Configuration()), new Timer());
        Servlet authCheck = new AuthCheckServlet(accountService);
        Servlet signIn = new SignInServlet(accountService);
        Servlet signOut = new SignOutServlet(accountService);
        Servlet signUp = new SignUpServlet(accountService);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(admin), "/admin/");
        context.addServlet(new ServletHolder(authCheck), "/api/v1/auth/check/");
        context.addServlet(new ServletHolder(signIn), "/api/v1/auth/signin/");
        context.addServlet(new ServletHolder(signOut), "/api/v1/auth/signout/");
        context.addServlet(new ServletHolder(signUp), "/api/v1/auth/signup/");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});

        Server server = new Server(port);
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
