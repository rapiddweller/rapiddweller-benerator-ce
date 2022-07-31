# Environment Files

An environment file may define an arbitrary number of systems to connect from Benerator via a short reference. 
Currently, relational databases and Kafka clusters and topics are supported. 
An environment file has the suffix `.env.properties`. 
As an example, you may define the development environment as `dev.env.properties`.

## System Definition
The settings for each system are listed in the environment file in the form:

```<name>.<type>.<setting>=<value>```

- `<name>` must be an identifier,

- `<type>` must be`db` or `kafka`,

- `<setting>` must be one of the settings supported by this system type. The name may contain '.' characters.

Example:
```properties
crm.db.url=jdbc:h2:mem:benerator
crm.db.driver=org.h2.Driver
```

## Additional Settings
Additional individual settings that depend on the environment may be declared. 
Such settings must not have a '.' character in their name.

Example:
```properties
id_strategy=hilo
```

## Comments

Lines starting with a hash # are considered comments and should be used to structure and explain the file's content:

```properties
# this is a comment line
```

## Full Environment File Example
Complete environment file example: `dev.env.properties`

```properties
# In the dev environment, the CRM system is located in a H2 databas
crm.db.url=jdbc:h2:mem:benerator
crm.db.driver=org.h2.Driver
crm.db.schema=PUBLIC
crm.db.user=sa
crm.db.read.only=true

# The procurement is a Kafka cluster on x.de
procurement.kafka.bootstrap.servers=x.de:9092
procurement.kafka.topic=orders
procurement.kafka.format=json

# Use hilo as default id generation strategy
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

## Planning for different environments

You will likely set up your data generation or anonymization task on a development environment, 
fine tune and test it and finally roll it out on a performance test, showcase or functional testing system. 
Consider these different stages of the Benerator project.

If you are planning and naming your environment definitions wisely, it is a trivial configuration 
change to switch from one stage to another:

1. Choose abstract names that describe the _roles_ of the systems, _not_ the names, eg. `crm`, `order`, `procurement` etc.

2. Define an environment file for each stage, eg. `dev`, `functest`, `perftest` etc.

3. In each environment files, assign each role identifier the physical system of that stage.

4. Create a Benerator setup in which `stage` occurs as a variable, eg. as setting, environment property or setting in an included configuration properties file

5. When declaring a system, use a script expression `{stage}` to reference the stage of a system

Now switching the stage means just switching the `stage` flag:

```xml
<setting name="stage" value="dev"/>
...
<database id=“custs“ environment=“{stage}“ system=“crm“/>
...
<kafka-exporter id=“orders“ environment=“{stage}“ system=“procurement“/>
```

## Backwards Compatibility
The previous database-only environment format (before Benerator 3.0.0) is still supported, 
but will be abandoned in a future release. 
