# Message Queue Access (Enterprise Edition)

Virtually any system that supports access via a JMS interface, 
can be used for data import or export in Benerator. 
It has been tested with ActiveMQ and RabbitMQ.


## Basic configuration

The base element of message queue access is a ```<jms-destination/>```. 
It must have each of the following attributes set:

| Name    | Description | example |
| ---     | --- | --- |
| id      | the internal identifier by which it can be accessed from other Benerator elements | 'orderQueue' |
| factory | the Java class used as JMS entry point of the used JMS library | 'org.apache.activemq.ActiveMQConnectionFactory' |
| url     | a URL specific for the used JMS library | 'tcp://localhost:61616' |
| name    | the public name of the queue | 'orderQueue' |
| type    | the type of queue, either 'topic' or 'queue' | 'queue' |
| format  | the format used for the messages, currently, 'json' or 'map' | 'json' |

As an example, an ActiveMQ topic of name 'newOrders' at the local system which uses 
JSON-formatted messages may be configured as

```xml
    <jms-destination id='userQueue' factory='org.apache.activemq.ActiveMQConnectionFactory' 
        url='tcp://localhost:61616' name='newUsers' type='topic' format='json' />
```

The internal id 'orderQueue' will later be used to access the queue for reading or writing.

Each JMS provider supports individual URL formats, some with variants for different purposes. 
Please refer to the JMS library documentation for details. 
Some JMS API providers use the word URI instead of URL, but don't let that confuse you.

For detailed information on ActiveMQ URL formats, 
see the '[ActiveMQ web page](https://activemq.apache.org/configuring-transports)',
for RabbitMQ, see the '[RabbitMQ web page](https://www.rabbitmq.com/jms-client.html)'.


## Export

In order to export data, the JMS destination defined above can be used as `consumer`:

```xml
    <generate type='person' count='10' consumer='userQueue'>
        <attribute name='firstName' pattern='Alice|Bob|Charly'/>
    </generate>
```


## Import

```xml
    <iterate source='userQueue' type='person' count='10' consumer='ConsoleExporter' />
```


## Playground

For getting acquainted with JMS access and play around on an offline system, you can use 
temporary ActiveMQ queues which are available without the hassle of installing ActiveMQ 
on your system. When using the URL `vm://localhost?broker.persistent=false`, 
the driver itself creates an n in-process queue, and you can experiment with writing data 
to it and retrieving it again.

So, here is an example setup which 

1. defines a queue
2. &lt;generate&gt;s person data and sends it to an ActiveMQ topic,
3. then &lt;iterate&gt;s the queue and prints the retrieved person data to the console

```xml
<setup>
    <jms-destination id='userQueue' factory='org.apache.activemq.ActiveMQConnectionFactory' 
        url='vm://localhost?broker.persistent=false' name='newUsers' type='topic' format='json' />
    
    <generate type='person' count='10' consumer='userQueue'>
        <attribute name="name" pattern="Alice|Bob|Charly"/>
        <attribute name="age" type="int" min="18" max="67"/>
    </generate>
    
    <iterate source='myqueue' type='person' count='10' consumer='ConsoleExporter' />
</setup>
```
