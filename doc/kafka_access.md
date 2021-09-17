# Kafka Access (Enterprise Edition)

Benerator can read from and write to Kafka queues.


## Basic configuration

The basic configuration elements which need to be provided for any kind of Kafka connector are

| Name | Description |
| --- | --- |
| id | the internal id by which the Kafka connector can be accessed in Benerator |
| bootstrap.servers | See [https://kafka.apache.org/documentation/#producerconfigs_bootstrap.servers]
| topic | The name of the Kafka queue to connect to |
| format | The message format used in this queue. Currently, only 'json' is supported |

Depending of the system you want to connect to, 
you might need to provide [Advanced configuration].


## Export

For exporting data, a ```<kafka-exporter>``` is used:

```xml
<setup>
    <kafka-exporter id='exporter' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json'/>
    <generate type='person' count='10' consumer='exporter'>
        <attribute name="name" pattern="Alice|Bob|Charly"/>
        <attribute name="age" type="int" min="18" max="67"/>
    </generate>
</setup>

```
## Import

For exporting data, a ```<kafka-importer>``` is used:

```xml
<setup>
    <kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json'/>
    <iterate source='importer' type='person' count='10' consumer='ConsoleExporter'/>
</setup>
```

## Advanced configuration

### Kafka properties

Benerator supports all configuration properties of Kafka 2.8.
For an in-depth explanation of these, please have a look at 
[https://kafka.apache.org/documentation/]

### encoding

By default, Benerator uses UTF-8 encoding to read and write JSON. 
If you need to use another encoding like UTF-16, specify it as ```encoding``` parameter:

```xml
<kafka-exporter id='exporter' bootstrap.servers='localhost:9094' topic='kafka-demo' 
    format='json' encoding='UTF-16'/>
```

### key.attribute

In order to tell a Kafka exporter which data to use as key to the exported message, 
specify the attribute name as ```key.attribute```, for example using an order priority as key:
```xml
<setup>
    <kafka-exporter id='exporter' bootstrap.servers='localhost:9094' key.attribute='priority' topic='kafka-demo' format='json'/>
    <generate type='order' count='100' consumer='exporter'>
        <attribute name='priority' type='string' script='(high|medium|low)'/>
        <attribute name='orderId' type='long' distribution='increment'/>
        <attribute name='productId' type='long' min='1' max='300'/>
    </generate>
</setup>
```

## Performance

Kafka commits its messages received. For faster read access to a Kafka queue, you can use 
the Kafka-Settings ```enable.auto.commit``` and ```auto.commit.interval.ms``` to combine 
a larger number of objects in one transaction, thus reducing transaction overhead. 

Example:

```xml
<kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json' 
    enable.auto.commit='true' auto.commit.interval.ms='5000'/>
```

For a better match with benerator's pageSize approach, you can use this in a Kafka-Importer too, 
eg. for committing messages in groups of 10,000:

```xml
<kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json' 
    page.size='10000'/>
```

### Encryption and Authentication

Ask your Kafka administrator on details how to configure secure access. 
All settings you might need are available in the XML format, 
your configuration could look like this:

```xml
<kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json'
        security.protocol='SSL'
        ssl.truststore.location='/var/private/ssl/kafka.client.truststore.jks' 
        ssl.truststore.password='test1234'
        ssl.keystore.location='/var/private/ssl/kafka.client.keystore.jks'
        ssl.keystore.password='test1234'
        ssl.key.password='test1234'
/>
```

Remember that you can hide your password from the XML setup file by using the 
properties file mechanism, see [Inclusion of properties files].
