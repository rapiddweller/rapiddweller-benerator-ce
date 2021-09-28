# Extending Benerator

Benerator can be customized in many ways. It provides Service Provider Interfaces which you can implement in Java for introducing own behavior. These
are

* **Generator** generates attribute data or entities with specific characteristics

* **Sequence** lets you define your own random or sequence algorithms

* **WeightFunction** allows you to provide a weight function that determines the probability of a certain value.

* **CumulativeDistributionFunction** allows you to provide a weight function that determines the probability of a certain value.

* **Converter** converts data of one type to another and can be used to support custom data classes, data formats or message formats.

* **Validator** validates previously defined data and tells if it is valid. This is useful for low-knowledge data generation where you have e.g. a
  validation library or system but little knowledge how to construct valid data. In this case, you can generate random data and let the validator
  decide, which to accept.

* **Consumer** receives the generated data and is typically used to store it in a file or system.

* **EntitySource** allows the user to import predefined entity data, e.g. from files with custom data formats.

* **DescriptorProvider** reads metadata from systems or files and provides them as Benerator descriptors. You can define an own DescriptorProvider for
  e.g. importing generation characteristics from annotations in an XMI file.

* **StorageSystem** provides access to a system for storing and querying data. This would be the interface to implement for connecting to your
  application, e.g. SAP, Siebel or a custom one.

* **Task** is an interface for executing custom Java code.

You can as well plug into the script frameworks that you are binding. So, for FreeMarker, you can implement custom methods in Java and call them from
the Benerator generation process.

## Custom Generators

### Generator Interface

**com.rapiddweller.benerator.Generator** is the basic Generator interface. It has the following methods:

* **Class`<E>` getGeneratedType()**: Tells the framework of which Java class the generated values are.

* **void init(GeneratorContext context)**: This is called to complete a Generators configuration and initialize it. If the configuration is not
  alright, the init() method is expected to throw an InvalidGeneratorSetupException. If the method finishes without exception, the generator has to be
  in state running. The next invocation of generate() is expected to return a valid product.

* **boolean wasInitialized()**: Tells, if the init() method has already been called. Since Benerator massively uses generator chaining and nesting,
  this is useful to avoid double initialization for each generator instance.

* **ProductWrapper`<E>` generate(ProductWrapper`<E>` wrapper)**: Generates an instance of the generic type E and uses the wrapper provided by the
  caller to return it to the client. If the method is called in an inappropriate state (
  created or closed), it throws an IllegalGeneratorStateException. If the generator is not available anymore, the method returns null.

* **void reset()**: Resets the generator to the initial state. When called, the Generator is expected to act as if '
  restarted'. After invocation the state has to be available.

* **void close()**: Closes the generator. After invocation the state is unavailable.

* **boolean isThreadSafe()**: Tells if the Generator class can safely be called concurrently by multiple threads.

* **boolean isParallelizable()**: Tells if the Generator can be cloned and each instance be executed with a dedicated single thread.

### Generator States

Generators have the following life cycle:

* **created**: The generator is under construction. This may take several steps, since generators need to be JavaBeans. When setup is done, a
  Generator must be initialized by calling its init() method.

* **running**: Generator construction is done and the generator is available. The user may use the Generator calling the generate() method.

* **closed**: The Generator may become unavailable automatically if its value space is depleted or manually when close()
  has been invoked. The Generator may be reset to the running state again by calling reset() . When being closed, the generator must be in a state in
  which it can be safely garbage collected.

### Helper classes for custom Generators

It is recommendable to make your custom generator extend one of the following classes:

* **AbstractGenerator**: Implements state handling

* **UnsafeGenerator**: Implements state handling and declares to be neither thread-safe nor parallelizable

* **ThreadSafeGenerator**: Implements state handling and declare to be thread-safe and parallelizable

When deriving a custom generator, prefer delegation to inheritance. This simplifies code maintenance and life cycle handling. Abstract Generator
implementations which already implement delegate handling are provided by the following classes:

* **GeneratorWrapper**: Wraps another generator of different product types

* **GeneratorProxy**: Wraps another generator of the same product type

* **MultiGeneratorWrapper**: Wraps multiple other generators, e.g. for composing their products, or arbitrarily choosing one of them for data
  generationGeneratorWrapper for wrapping a single delegate generator of different product type.

## Custom FreeMarker methods

You can define custom functions in Java which will be called by FreeMarker, e.g. a helloWorld function which could be used like this:

`<attribute name="greeting" script="{helloWorld(Volker)}"/>`

See the FreeMarker documentation at http://freemarker.sourceforge.net/docs/pgui_datamodel_method.html for some more details. The Java implementation
of the class could be something similar to this:

```java
public class HelloWorldMethod implements TemplateMethodModel {

public TemplateModel exec(List args) throws TemplateModelException {return new SimpleString("Hello " + args[0]);}

}
```

A descriptor file would need to instantiate the class, before it can be called:

`<bean id="helloWorld" class="HelloWorldMethod" />`

Unfortunately, anything you provide in the method call will be converted to a List of Strings, so date or number formatting may be necessary on the
descriptor side and String parsing on the Java Method side. If the result type of the method is not a SimpleString the same conversions will be done.
so you might need to use strange expressions, e.g. for a method that sums up dates and returns a date:

`<attribute name="lastupdatedtime" script="${dateSum(deal.created_time?string('yyyy-MM-dd'), deallog._delay)?string('yyyy-MM-dd')}"/>`

## Custom Sequences

Since a Sequence's primary concern is number generation, a Sequence implementor can focus on number generation. By intheriting from the class
Sequence, one inherits the data redistribution feature defined in this class and only needs to implement

`<T extends Number>` Generator`<T>` createGenerator(Class`<T>` numberType, T min, T max, T granularity, boolean unique);

The method needs to be implemented in a way that it creates and returns a new Generator component, which generates numbers of the given numberType
with a numerical value of at least min and at most max and a granularity, such that each generated value x is x = min + n * granularity, with n being
an integral number. If the caller of this method indicated that it requires unique number generation and the Sequence is not capable of generating
unique numbers, the method is requires to throw a `com.rapiddweller.ConfigurationError`.

## Custom WeightFunctions

A WeightFunction specifies a probability density for a numeric parameter. If applied to a number generator it will generate numbers with the same
probability density. When applied to collections or arrays, the function will evaluate to the probability of an element by its index number.

For defining your own WeightFunction, you just need to implement the WeightFunction interface:

```java
public interface WeightFunction extends Distribution { double value(double param);}
```

Attention: Take into account, that the parameter may become zero: When using the Function for weighing the entries of an import file or a list, the
function will be called with zero-based indexes as argument. So, if you want to use a 10,000-element CSV-file weighted by a custom WeightFunction, it
must be able to produce useful values from 0 to 9,999.

Example:

```java
public class GaussianFunction extends com.rapiddweller.benerator.distribution.AbstractWeightFunction {

  private final double average;

  private final double deviation;

  private final double scale;

  public GaussianFunction(double average, double deviation) {
    this.average = average;
    this.deviation = deviation;
    this.scale = 1. / deviation / Math.sqrt(2 * Math.PI);
  }

  @Override
  public double value(double param) {
    double x = (param - average) / deviation;
    return scale * Math.exp(-0.5 * x * x);
  }

}
```


## Applying a Weight Function

You can weigh any arbitrary imported or numeric data by a Weight Function. A Weight Function is defined by a class that implements the interface
`com.rapiddweller.model.function.WeightFunction`:

```java
public interface WeightFunction extends Weight {

double value(double param);
    ...
}
```

When using a weight function, Benerator will serve data items in random order and as often as implied by the function value. Benerator automatically
evaluates the full applicable number range (as defined by numerical min/max or number of objects to choose from) and normalize the weights. There is
no need to provide a pre-normalized distribution function. You may define custom Weight Functions by implementing the WeightFunction interface.

## Custom Converters

Custom Converters can be used for supporting custom data types or specific data formats. The Converter interface has the following methods:

* **Class`<S>` getSourceType()**: Returns the Java class whose instances can be converted

* **Class`<T>` getTargetType()**: Returns the Java class to which objects are converted

* **T convert(S sourceValue)**: Converts a sourceValue of type S to an object of type T

* **boolean isThreadSafe()**: Tells if a converter can be executed with several concurrent threads

* **boolean isParallelizable()**: Tells if a converter can be cloned and each clone can run with a single dedicated thread

These classes are useful parent classes for a custom Converter implementation:

* UnsafeConverter: Declares to be neither thread-safe nor parallelizable

* ThreadSafeConverter: Declares being thread-safe and parallelizable

Beyond this, a custom Converter should

* provide a public default (no-arg) constructor

* exhibit each relevant property by a public set-method

## Custom Validators

A validator must implement at least one of these two interfaces:

* javax.validation.ConstraintValidator (as of JSR 303 - Bean Validation)

* com.rapiddweller.common.Validator

Beyond this, a custom validator should

* provide a public default (no-arg) constructor

* make each relevant property configurable by a set-method

### javax.validation.ConstraintValidator

For implementing ConstraintValidators, see the Bean Validation documentation.

### com.rapiddweller.common.Validator

For implementing the `com.rapiddweller.common.Validator` interface, a custom validator must implement the method boolean valid(E object) method
returning `true` for a valid object, `false` for an invalid one.

It is recommended to inherit a custom Validator from the class com.rapiddweller.common.validator.AbstractValidator. If the Validator interface will
change in future versions, the AbstractValidator will try to compensate for implementations of the old interface. Thus, a simple validator
implementation which checks that an object is not null would be:

```java
public class NotNullValidator`<E>` extends com.rapiddweller.common.validator.AbstractValidator`<E>` {

public boolean valid(E object) { return (object != null); }

}
```


### Implementing both interfaces

If you inherit a custom Validator from `com.rapiddweller.common.validator.bean.AbstractConstraintValidator`, it implements both interfaces.

## Custom Consumers

A Consumer is the final destination that receives the data generated by Benerator. It implements the Consumer interface with the following methods:

* **startConsumption(ProductWrapper`<E>` object)** starts processing of a data object (usually an Entity)

* **finishConsumption(ProductWrapper`<E>` object)** finishes processing of a data object.

* **flush()** forces the consumer to forward or persist its data.

* **close()** closes the consumer

Consumers must be thread-safe.

Beyond this, it should

* provide a public default (no-arg) constructor

* make each relevant property configurable by a set-method

If your data format and generation supports nesting, the methods startConsuming() and finishConsuming() are called in a hierarchical manner. So, if A
contains B, the invocation sequence is:

startConsuming(A)

startConsuming(B)

finishConsuming(B)

finishConsuming(A)

For non-hierarchical processing you only need to implement the startConsuming() with data processing logic.

## Custom EntitySources

For defining a custom EntitySource you need to implement two methods:

* Class`<Entity>` getType(): Must return Entity.class

* DataIterator`<E>` iterator(): Must return a DataIterator which iterates over entities. If the iterator requires resource allocation, it should free
  its resources on close() invocation.

Beyond this, a custom EntitySource should

* provide a public default (no-arg) constructor

* make each relevant property configurable by a set-method

## Custom Tasks

For executing custom Java code, the most efficient choice is to write a JavaBean class that implements the interface com.rapiddweller.task.Task.

The Task interface consists of callback interfaces for execution logic and lifecycle management.

* **String getTaskName()**: returns a task name for tracking execution. On parallel execution, the framework appends a number to identify each
  instance.

* **TaskResult execute(Context context, ErrorHandler errorHandler)**: implements the core functionality of the task. Tasks may be called several times
  subsequently and the task uses return valueto indicate whether it is running, has just finished or is unavailable.

* **void pageFinished()**: is called by the framework to inform the task that a 'page' of user-defined size has finished. This can be used, e.g. for
  grouping several execute() steps to one transaction.

* **void close()**: is called by the framework to make the task close all resources and prepare to be garbage-collected.

* **boolean isParallelizable()**: Tells if the task can be executed in several threads concurrently. This is not used in Benerator.

* **boolean isThreadSafe()**: Tells if it is possible to create clones of the task and execute each clone in a dedicated single thread. This is not
  used in Benerator.

Benerator provides several implementations of the Task interface which are useful as parent class for custom implementations:

* AbstractTask is a simple abstract implementation that provides useful default implementations for the lifecycle-related methods.

* TaskProxy can be used to wrap another Task object with a custom proxy and add extra functionality.

* RunnableTask can be used to wrap an object that implements the Runnable interface.

A task has access to all Java objects and entities in the context and may arbitrarily add own objects (e.g. for messaging between different tasks). A
common usage pattern is to share e.g. a transactional database by the context, have every thread store objects in it and then use the pager to commit
the transaction.