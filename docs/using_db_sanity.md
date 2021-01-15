# Using DB Sanity 

You can call Databene DB Sanity for checking preconditions before starting data generation and for verifying properness of the generated data in the end. For detailed information about DB Sanity, check its project homepage: [http://databene.org/dbsanity](http://databene.org/dbsanity). To use its functionality in Benerator, download and install dbsanity4ben from [http://databene.org/dbsanity4ben](http://databene.org/dbsanity4ben).

For calling DB Sanity in a Benerator descriptor file, you define the checks and put them into a single XML file or distribute them over several ones and put them into a sub directory of your Benerator project, typically called 'dbsanity'.

Import the plugin functionality to your Benerator project using

``` xml
<import platform="dbsanity" … />
```

Then you can call DB Sanity providing a reference to a database you have declared before:

``` xml
<database id="db" … />
<dbsanity database="db" />
```

Alternatively, you can specify the environment and use a new database connection to perform data verification:

```xml
<dbsanity environment="mytestdb" />
```

You have the following configuration options:

| Option | Description | Default Value |
| --- | --- | --- |
| environment | The environment name with the configuration of the database to verify (see the 'database' chapter about environment definition). | - |
| database | The database to verify | - |
| in | The directory from which to read the | dbsanity |
| out | The directory in which to put the report | dbsanity-report |
| appVersion | An application version for which to perform the checks | * |
| tables | Comma-separated list of tables on which to restrict the checks | - |
| tags | Comma-separated list of tags on which to restrict the checks | - |
| skin | The DB sanity skin to use for reports | online |
| locale | The locale in which to render values | default locale |
| mode | DB Sanity's execution mode | default |
| onError | Configures how to react to a requirements violation | See the chapter 'Error Handling' |