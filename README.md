# Queue Service

Message Queues
--------------
Task is to design and implement a message queue.


Background
----------
Message queues are a ubiquitous mechanism for achieving horizontal scalability.
However, many production message services (e.g., Amazon's SQS) do not come with
an offline implementation suitable for local development and testing.  The
context of this task is to resolve this deficiency by designing a simple
message-queue API that supports three implementations:

 - an in-memory queue, suitable for same-JVM producers and consumers;

 - a file-based queue, suitable for same-host producers and consumers, but
   potentially different JVMs; and

 - an adapter for a production queue service, such as SQS.

The intended usage is that application components be written to use queues via
a common interface, and injected with an instance suitable for the environment
in which that component is running (development, testing, integration-testing,
staging, production, etc).

Behavior
--------
If message queues are an unfamiliar concept, see SQS docs for a description of
how they are intended to behave.  In particular, note the following properties:

 - multiplicity
   A queue supports many producers and many consumers.

 - delivery
   A queue strives to deliver each message exactly once to exactly one consumer,
   but guarantees at-least once delivery (it can re-deliver a message to a
   consumer, or deliver a message to multiple consumers, in rare cases).

 - order
   A queue strives to deliver messages in FIFO order, but makes no guarantee
   about delivery order.

 - reliability
   When a consumer receives a message, it is not removed from the queue.
   Instead, it is temporarily suppressed (becomes "invisible").  If the consumer
   that received the message does not subsequently delete it within within a
   timeout period (the "visibility timeout"), the message automatically becomes
   visible at the head of the queue again, ready to be delivered to another
   consumer.

# GOAL 

Message queues are a ubiquitous mechanism for horizontal scalability.
The context of this implementation is to design a simple
message-queue API that supports three implementations:

 - an in-memory queue, suitable for same-JVM producers and consumers;
 - a file-based queue, suitable for same-host producers and consumers, but
   potentially different JVMs; and
 - an adapter for a production queue service, such as SQS.


## Installation and Usage

* Run "mvn clean install -P [environment] "
* Possible environments are: local, dev, test, stage, prod
* Default profile is local
* If you use Maven, add the dependency in your pom.xml

		<dependency>
			<groupId>com.example</groupId>
			<artifactId>queue-service</artifactId>
			<version>1.0.1</version>
		</dependency>

## TODOs

 - Environment-values should be Spring Autowired in Constructor or Setter Injection
 - Get A on CodeClimate
 - Add more JavaDoc
 - Add more tests
