# Using Benerator as Load Generator

Benerator has done the first implementation steps towards being itself used as load generator, not only data generator for preparing load test data.

The basic idea behind the approach is to use a `<generate>` element for composing invocation parameters, and use special 'Consumer' or 'Task'
implementations for doing the actual invocation of the system under test.

Performance of task execution can be tracked, setting a stats attribute to true:

```xml
<run-task class="com.my.TaskImpl" invocations="10000" threads="100" stats="true" />
```

The performance of consumer invocations can be tracked, using the PerfTrackingConsumer class:

```xml
<import platforms="contiperf" />

<bean id="myWS" spec="new MyWebServiceConsumer(...)"/>

<generate type="params" count="10000">
    <value type="int" min="100" max="10000" distribution="random"/>
    <consumer spec='new PerfTrackingConsumer(myWS)'/>
</generate>
```

## JavaInvoker

To aid you calling Java code from Benerator, there is a Helper class, JavaInvoker, which implements the Consumer interface and calls a Java method,
which is declared by its name, on a Java object. The data of the generated entity is automatically:

```xml
<bean id="service" spec="..." />

<bean id="invoker" spec="new JavaInvoker(ejb, 'enrolCustomer')" />
```

For tracking invocation performance, you need to add the PerfTrackingConsumer:

```xml
<bean id="enrolCustomer" class="PerfTrackingConsumer" >
    <property name="target" value="{invoker}" />
</bean>
```

## Checking performance requirements

You can as well define performance requirements using properties of the class PerfTrackingConsumer:

| Property | Description |
| --- | --- |
| max | the maximum number of milliseconds an invocation may take |
| percentiles | a comma-separated list of percentile requirements in the format used in ContiPerf |

By default, execution times are printed to the console. You can as well plug in custom ExecutionLoggers for saving log data to file or feed it to
other applications. This is done using the PerfTrackingConsumer's executionLogger property.

An example for the properties described above:

```xml
<import class="com.rapiddweller.contiperf.report.ConsoleReportModule" />

<bean id="enrolCustomer" class="PerfTrackingConsumer" >
    <property name="target" value="{invoker}" />
    <property name="executionLogger" value="{new ConsoleExecutionLogger()}" />
    <property name="max" value="5000" />
    <property name="percentiles" value="90:5000, 95:7000"/>
</bean>
```

