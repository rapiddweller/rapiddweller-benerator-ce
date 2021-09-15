# JSON File Generation and Anonymization (Enterprise Edition)

## Iterating entity data from a JSON file

You can iterate entity data from a JSON file by assigning the file with the extension 
'.json' and specifying the file name as 'source' in an `<iterate>` statement, 
e.g. for printing the data to the console:

```xml

<iterate type="user" source="users.json" consumer="ConsoleExporter"/>
```

An example source file 'users.json' may look like this:

```json
[
    {
        "name":"Alice",
        "age":23
    },
    {
        "name":"Bob",
        "age":34
    },
    {
        "name":"Charly",
        "age":45
    }
]
```


## Creating JSON files

Generated data is exproted to a JSON file by defining a consumer like this:```consumer="new JsonFileExporter('gen-persons.json')"```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<setup>
    <generate type="person" count="5" consumer="new JsonFileExporter('gen-persons.json')">
        <attribute name="name" pattern="Alice|Bob|Charly"/>
        <attribute name="age" type="int" min="18" max="67"/>
    </generate>
</setup>
```

