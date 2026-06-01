package io.openliberty.example.messaging.rest;

import java.util.logging.Logger;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

@ApplicationScoped
@Path("/hello")
public class HelloWorldResource {

    private static Logger logger = Logger.getLogger(HelloWorldResource.class.getName());
    private FlowableEmitter<Message<String>> helloWorldEmitter;

    @GET
    public String hello() {
        return "Hello, World!";
    }

    public static record InnerHelloWorldRequest(String name) {
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void post(InnerHelloWorldRequest name) {
        logger.info("Received name: " + name);
        helloWorldEmitter.onNext(Message.of("Hello, %s!".formatted(name.name())));
    }

    @Outgoing("helloWorld")
    public Publisher<Message<String>> sendHelloWorld() {
        Flowable<Message<String>> flowable = Flowable.create(emitter -> {
            this.helloWorldEmitter = emitter;
        }, BackpressureStrategy.BUFFER);
        return flowable;
    }
}
