# Kafka Access (Enterprise Edition)

Benerator can read from and write to Kafka queues.

## Basic configuration

The basic configuration elements required for any kind of Kafka connector are

| Name | Description |
| --- | --- |
| id | the internal id by which the Kafka connector can be accessed in Benerator |
| bootstrap.servers | See the original [Kafka Documentation](https://kafka.apache.org/documentation/#producerconfigs_bootstrap.servers)
| topic | The name of the Kafka queue to connect to |
| format | The message format used in this queue. Currently, only 'json' is supported |

Depending on the system you want to connect to, 
you might need to provide [Advanced configuration](#advanced-configuration).


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

For importing data, a ```<kafka-importer>``` is used:

```xml
<setup>
    <kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json'/>
    <iterate source='importer' type='person' count='10' consumer='ConsoleExporter'/>
</setup>
```

## Advanced configuration

### Kafka properties

Benerator supports all configuration properties of Kafka 2.8.
For an in-depth explanation of these, please have a look at the 
'[Kafka Documentation](https://kafka.apache.org/documentation/)'.

### encoding

By default, Benerator uses UTF-8 encoding to read and write JSON. 

If you need to use another encoding like UTF-16, specify it as `encoding` parameter:

```xml
<kafka-exporter id='exporter' bootstrap.servers='localhost:9094' topic='kafka-demo' 
    format='json' encoding='UTF-16'/>
```

### auto.offset.reset

When connecting to a topic, a client declares which group it belongs to
and the group is assigned a message offset, which is stored on the server. 
When messages with a higher offset exist in the queue or are sent to it, 
the Kafka server sends them to the client and increases the saved offset.

The client declares its group using the setting `group.id`, which Benerator 
sets to `default`, if not provided explicitly.

When a client reconnects, the Kafka server remembers the client group's 
offset and delivers the new messages.

This is the good case. In some situations, this method has issues:

- New client group

- Topic was deleted and recreated

- messages have been deleted

- You name it

Therefore, a client must declare an offset reset strategy (`auto.offset.reset`) 
to be applied in such a case. The setting can have the following values

- `earliest`: automatically reset the offset to the earliest offset

- `latest`: automatically reset the offset to the latest offset

- `none`: throw exception to the consumer if no previous offset is found for the consumer's group


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

### key.serializer and key.deserializer

For de/serializing of message keys, Kafka provides different classes, 
each defined in Kafka's package `org.apache.kafka.common.serialization`.
For sending messages a serializer is used, for receiving messages a 
deserializer.

By default, Benerator uses Kafka's `StringSerializer` and `StringDeserializer`.

If necessary, you can use other de/serializer classes, 
specifying the `key.serializer` or `key.deserializer` setting with one of these:

- org.apache.kafka.common.serialization.ByteArraySerializer or ByteArrayDeserializer

- org.apache.kafka.common.serialization.BytesSerializer or BytesSerializer

- org.apache.kafka.common.serialization.DoubleSerializer or DoubleDeserializer

- org.apache.kafka.common.serialization.FloatSerializer or FloatDeserializer

- org.apache.kafka.common.serialization.IntegerSerializer or IntegerDeserializer

- org.apache.kafka.common.serialization.LongSerializer or LongDeserializer

- org.apache.kafka.common.serialization.ShortSerializer or ShortDeserializer

- org.apache.kafka.common.serialization.StringSerializer or StringDeserializer

Alternatively, you can implement and provide your own de/serializer.


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
e.g. for committing messages in groups of 10,000:

```xml
<kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json' 
    page.size='10000'/>
```


## Encryption and Authentication

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

If the Benerator file is shared, but your access individual, 
then remember that you can hide your password from the XML setup file by using the 
properties file mechanism in your user folder, see 
'[&lt;setting&gt; and Benerator identifiers](data_generation_concepts.md#setting-and-benerator-identifiers)':

Local file kafka.properties:
```
truststore_password=test1234
keystore_password=test1234
ssl_password=test1234
```

Shared file benerator.xml:

```xml
<include uri="kafka.properties"/>
<kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json'
        security.protocol='SSL'
        ssl.truststore.location='/var/private/ssl/kafka.client.truststore.jks' 
        ssl.truststore.password='{truststore_password}'
        ssl.keystore.location='/var/private/ssl/kafka.client.keystore.jks'
        ssl.keystore.password='{keystore_password}'
        ssl.key.password='{ssl_password}'
/>
```