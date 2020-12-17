Instructions
------------

Start XML file generation by typing 'mvn benerator:createxml' on the command line.
With the default settings, Benerator creates 3 XML files based on the schema file 'transactions.xsd'.
Edit the file 'pom.xml' plugin section for changing invocation parameters or edit 'transactions.xsd'
for modifying characteristics of the generated data.
If you need to use another logging framework, change the 'pom.xml's dependency to slf4j-log4j12 and log4j.