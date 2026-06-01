package io.openliberty.example.messaging.rest;

import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Path("/hello")
public class HelloWorldResource {

    private static Logger logger = Logger.getLogger(HelloWorldResource.class.getName());
    private String currentValue = "world";

    @GET
    public String hello() {
        return "Hello, World!";
    }

    @GET
    @Path("/current")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCurrentValue() {
        return currentValue;
    }

    @Incoming("helloWorld")
    public void consumeHelloWorld(String message) {
        logger.info("Received message: " + message);
        this.currentValue = message;
    }
}
