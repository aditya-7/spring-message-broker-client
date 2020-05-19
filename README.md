# Spring Message Broker Client Library
A client that lets you choose between a set of Message brokers for inter-service communication.
This can be plugged in as a dependency in a Spring boot project, and a developer can build applications agnostic of the broker running behind the scenes.
It currently supports Active MQ and Apache Kafka in the backend.

## How to Use 
Add the following code snippet to your application's pom.xml.

```python
<dependency>
	<groupId>com.ltts</groupId>
	<artifactId>spring-msg-broker-client-lib</artifactId>
	<version>${VERSION}</version>
</dependency>
```

Configure the topics in Spring Boot as you would for Apache Kafka or Active MQ, and autowire the MessageBrokerClient interface. Implement the listener for the Spring Boot ServiceMessageEvent and handle all the messages received on the topic channel here. Use the produce method of the MessageBrokerClient implementation to send messages through a topic channel.

## Authors
* **Aditya Kishore** - *aditya.kishore@ltts.com*
* **Madiwalappa** - *madiwalappa.udachan@ltts.com*
* **Saikumar Gajji** - *gajji.saikumar@ltts.com*
