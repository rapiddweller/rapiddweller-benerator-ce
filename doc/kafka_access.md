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
<kafka-importer id='importer' bootstrap.servers='localhost:9094' topic='kafka-demo' format='json'/>
<iterate source='importer' type='person' count='100' threads='2' consumer='ConsoleExporter'/>
```

## Advanced configuration

TODO page.size

TODO check code for elementary Benerator configs

Benerator supports all configuration properties of Kafka 2.8.
For an in-depth explanation of these, please have a look at 
[https://kafka.apache.org/documentation/]