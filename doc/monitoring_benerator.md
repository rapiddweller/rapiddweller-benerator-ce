# Monitoring Benerator

You can monitor Benerator using a JMX client, for example, JConsole.

The following properties can be monitored:

| Property | Description |
| --- | --- |
| TotalGenerationCount | The total number of generated data sets |
| CurrentThroughput | The number of data sets generated per second |
| OpenConnectionCount | The number of currently open database connections |
| OpenResultSetCount | The number of currently open database query result sets |
| OpenStatementCount | The number of currently open database statements |
| OpenPreparedStatementCount | The number of currently open prepared database statements |

The first two properties, **TotalGenerationCount** and **CurrentThroughput**, are used for Benerator performance monitoring and optimization. If you
suspect Benerator to be 'hanging', first check its **CurrentThroughput**.

The last four properties (**Open...**) for database resource monitoring and database resource leak detection.

## Monitoring with JConsole

1. Start JConsole on the command line

2. Select a process

3. Choose the MBeans tab

4. In the left tree view, select

5. benerator – monitor – Attributes

6. Select the attribute TotalGenerationCount or CurrentThroughput and the value is displayed on the right

7. Double-clicking the number opens a chart that displays the value's evolution over time

![](assets/grafik21.png)

## Remote monitoring

For monitoring Benerator execution from a remote machine, you need to set some BENERATOR_OPTS. Here are only the simplest and basic settings.

**Warning:** **The settings described here do not provide any security and thus are recommended only for evaluation!**
If you do want to monitor a sensitive system in a remote manner, you need to apply security settings as described
in [https://docs.oracle.com/en/java/javase/11/management/monitoring-and-management-using-jmx-technology.html](https://docs.oracle.com/en/java/javase/11/management/monitoring-and-management-using-jmx-technology.html)!

These are server-side settings and are independent of the client you are using:

| Option | Description |
| --- | --- |
| -Dcom.sun.management.jmxremote | Enable remote access |
| -Dcom.sun.management.jmxremote.port=9003 | Configures the port for remote access |
| -Dcom.sun.management.jmxremote.authenticate=false | Enables anonymous and unsecure access (not recommended) |
