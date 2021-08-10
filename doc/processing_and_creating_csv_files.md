# Processing and creating CSV Files

## Iterating entity data from a CSV file

You can iterate entity data from a CSV file by assigning the file with the extension '.ent.csv' and specifying the file
name as 'source' in an `<iterate>` statement, e.g. for printing the data to the console:

```xml

<iterate type="user" source="user.ent.csv" consumer="ConsoleExporter"/>
```

This way, you need to have a CSV file which uses column headers and the default column separator (which is comma by
default and can be set globally in the root element's defaultSeparator attribute, e.g. to a
semicolon: `<setup defaultSeparator=";">`)

If the CSV file does not have headers or uses another separator or file encoding that deviates from the default, you
need to configure the CSV import component (CSVEntitySource) explicitly with a `<bean>` statement and refer it later:

```xml

<setup>

    <bean id="in" class="CSVEntitySource">
        <property name="uri" value="headless-in.csv"/>
        <property name="separator" value=";"/>
        <property name="encoding" value="UTF-8"/>
        <property name="columns" value="name,age"/>
    </bean>

    <iterate type="user" source="in" consumer="ConsoleExporter"/>
</setup>
```

For CSV files without header, you need to specify a comma-separated list of column names in the 'columns' property.

## Creating CSV files

For creating a CSV file you must always take the same approach as above: Defining a bean with its properties and
refering it as consumer:

```xml

<setup>

    <bean id="out" class="CSVEntityExporter">
        <property name="uri" value="target/headless-out.csv"/>
        <property name="columns" value="name, age, check"/>
    </bean>

    <generate type="product" count="200" consumer="out"/>
</setup>
```

See the component documentation of

CSVEntityExporter

for more details.