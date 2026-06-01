# OpenLiberty Messaging with Kafka

### Motivation

I built this demo app to gain an understanding of the internals of how MicroProfile Messaging works with Kafka and other message queues. I know have a better understanding of the systems which work under the hood.

Main guide help assistance: https://openliberty.io/guides/microprofile-reactive-messaging-acknowledgment.html#implementing-the-manual-acknowledgment-strategy

### how this works

We've got the following projects: `rest-publisher`, `consumer-group-a` and `consumer-group-b`

We've also got 5 containers which get spawned: the kafka instance, the `rest-publisher` instance, 2 instances of `consumer-group-a` and 1 instance of `consumer-group-b`

When we send a post request using [send-message.sh](./send-message.sh) with a name to the `rest-publisher`, it will send a message to the kafka instance. Then the kafka instance will send it to each of the consumer groups (acting as a Pub-Sub broker). 

We've got 2 consumer groups, `consumer-group-a` and `consumer-group-b`. Since we've got 2 instances of `consumer-group-a`, it will only send a message to one of those instances in `consumer-group-a` (acting as a load balancer/message queue).

### Things learned

When working with kafka, if you want to have only 1 consumer receiving the message, you need to have those instances of your app using the same consumer group.

If you want all of them to receive the message, they need to be in different consumer groups.

### What we can do next:

For proper "worker-pool" behavior, we need to ensure that we only "acknowledge" the message once it's been processed. This can be done by doing something like this:

```java
@Incoming("propertyRequest")
@Acknowledgment(Acknowledgment.Strategy.MANUAL)
public void processProperty(Message<String> propertyMessage) {
    // process the message
    //...
    
    // ack the message
    propertyMessage.ack();
    // do other stuff
}
```

This would be useful for worker pools, where we want to ensure that we only process the message once it's been processed. 

### Testing this behavior

Run:
```bash
./gradlew build
```

Then:
```bash
docker compose up --build
```

Then you'll have the following running:
- publisher running on http://localhost:9080/api/hello
- `consumer-group-a` running on both http://localhost:9081/api/hello/current and http://localhost:9082/api/hello/current
- `consumer-group-b` running on http://localhost:9083/api/hello/current.

Then you can run the following command to see the behavior:
```
./send-message.sh <your name>
```
and watch on the browser to see the changes.