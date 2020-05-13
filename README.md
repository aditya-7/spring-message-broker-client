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
		<version>1.0.0</version>

</dependency>
```

## Authors
* **Aditya Kishore** - *aditya.kishore@ltts.com*
* **Madiwalappa** - *madiwalappa.udachan@ltts.com*
* **Saikumar Gajji** - *gajji.saikumar@ltts.com*
