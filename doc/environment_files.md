# Environment Files

An environment file may define an arbitrary number of systems to connect from Benerator via a short reference. Currently, relational databases and Kafka clusters and topics are supported. An environment file has the suffix .env.properties. As an example, you may define the development environment as „dev.env.properties“.

## System Definition
The settings for each system are listed in the environment file in the form:

```<name>.<type>.<setting>=<value>```

- `<name>` must be an identifier,

- `<type>` must be`db` or `kafka`,

- `<setting>` must be one of the settings supported by this system type. 

Example:
```properties
crm.db.url=jdbc:h2:mem:benerator
crm.db.driver=org.h2.Driver
```

## Additional Settings
Additional individual settings that depend on the environment may be declared. 
It is recommended to avoid '.' characters in their names.

Example:
```properties
id_strategy=hilo
```

## Full Environment File Example
Complete environment file example: `dev.env.properties`

```properties
crm.db.url=jdbc:h2:mem:benerator
crm.db.driver=org.h2.Driver
crm.db.schema=PUBLIC
crm.db.user=sa
crm.db.read.only=true

procurement.kafka.bootstrap.servers=x.de:9092
procurement.kafka.topic=orders
procurement.kafka.format=json

id_strategy=hilo
```

## Placing and Locating Environment Files
You can put the environment file in different places. 
If it should be shared with other users of the same project, 
it is recommended to put it in a folder named 'conf' inside the project folder 
(example location `myproject/conf/dev.env.properties`). 

For private system and password configurations, you can put an environment file 
into the folder `rapiddweller` under your user home directory 
(example location `~/rapiddweller/dev.env.properties` on a macos/Unix system 
or `C:\Users\<your_user_name/rapiddweller/dev.env.properties>` on a Windows system).

Beware: For referencing an environment, have it in the proper folder and 
use just the environment name to reference it (not a path!)

## Accessing a System in an Environment

In order to access the system, you need to reference it in the benerator setup. 
The `crm` database above would be referenced by:

```xml
<database id=“custs“ environment=“dev“ system=“crm“/>
```

or

```xml
<kafka-importer id=“orders“ environment=“dev“ system=“procurement“/>
```

You may set up a Benerator task to run on different environments by making the environment 
a variable (Defined in an <include>d properties file or as a <setting>) and reference it, 
for example like

```xml
<setting name="stage" value="dev"/>
...
<database id=“custs“ environment=“{stage}“ system=“crm“/>
...
<kafka-importer id=“orders“ environment=“{stage}“ system=“procurement“/>
```

## Backwards Compatibility
The previous database-only environment format (before Benerator 2.1.0) is still supported, 
but will be abandoned in a future release. 
