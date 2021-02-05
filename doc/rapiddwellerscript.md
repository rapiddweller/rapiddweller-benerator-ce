# rapiddwellerScript

## Motivation

[rd-lib-script](https://github.com/rapiddweller/rd-lib-script) former DatabeneScript by Volker Bergmann is a script language designed for simplifying test data generation. Text-targeted template languages like FreeMarker and general
languages like JavaScript have specific problems, which can be avoided by a language tailored to the task of data generation.

rapiddwellerScript uses many elements familiar from C-style languages like Java and C#, but adds some specials:

* More convenient object construction

* intuitive date arithmetic

* seamless integration and interaction with Benerator

## Examples

### Variable definition

```xml
<execute>`x = 3`</execute>
```

### Variable assignment

```xml
<execute>`x = x + 1`</execute>
```

### Object construction

Constructor-based:

`new MyGenerator('alpha.txt', 23)`

Properties-based:

`new MyGenerator { filename='alpha.txt', limit=23 }`

### Date arithmetic

`new Date() + 5000`

### Java integration

`(com.rapiddweller.common.SystemInfo.isWindows() ? 'win' : 'other')`

Benerator integration

`(context.contains('key') ? 'def' : 'none')`

## Syntax

### Comments

Line comments start with // and include the rest of the line.

Normal comments begin with /* and end with */

### White Space

Spaces, tabs, CR, LF, \u000C

### Data Types

Signed integral numbers: long, int, short, byte, big_integer

Signed floating point numbers: double, float, big_decimal

Alpha: string, char

Date/time: date, timestamp

Other: boolean, object (Java object), binary (byte[])

### Identifiers

The first character must be an ASCII letter or underscore. An arbitrary number of subsequent characters may be ASCII letters, underscores or numbers.

Legal identifiers: _test, A1234, _999

Illegal identifiers: 1ABC, XÖ, F$D, alpha.beta

### Escape Sequences

\bBackspace

\tTab

\nNew Line

\rCarriage Return

\fForm Feed

\"Double quote

\'Single quote

\nnnOctal encoded character

### String Literal

Quoted with single quotes, e.g. `Text`

### Decimal Literal

Supporting decimal syntax.

Legal decimal values: `1234.2345E+12`, `1234.2345E12`, `1234.2345e-12`

### Integral Number Literal

Supporting decimal, hexadecimal and octal syntax.

Legal decimal values: `0`, `123`

Legal octal values: `01`, `00123`

Legal hexadecimal values: `0x0dFa`

### Boolean Literal

Legal values: `true`, `false`

### null Literal

Legal value: `null`

### Qualified name

`identifier(.identifier)*`

Legal values: `com.rapiddweller.benerator.Generator, Generator`

### Constructor invocation

Works like in Java, e.g.

```java
new MyGenerator('alpha.txt', 23)
```

### Bean construction

Instantiates an object by the class' default constructor and calling property setters, e.g.

`new MyGenerator { filename='alpha.txt', limit=23 }`

is executed like

```java
MyGenerator generator = new MyGenerator();

generator.setFilename("alpha.txt");

generator.setLimit(23);
```

### Method invocation

Can occur on static Java methods on a class or instance methods on an object.

Static method invocation: `com.my.SpecialClass.getInstance()`

instance method invocation: `generator.generate()`

### Attribute access

Can occur on static Java attributes on a class or instance methods on an object.

Static attribute access: `com.my.SpecialClass.instance`

instance attribute access: `user.name`

### Casts

Benerator can casts data types. Cast arguments are Benerator's simple types.

Examples: `(date) '2009-11-23'`, `(long) 2.34`

### Unary Operators

Arithmetic Negation: `-`

Bitwise complement: `~`

Boolean Negation: `!`

### Arithmetic Operators

Multiplication: `*`

Division: `/`

Modulo division: `%`

Addition: +, e.g. `new Date() + 5000`

Subtraction: `-`

### Shift Operators

Left shift: `<`< (in XML descriptor files use &lt;&lt;)

Right shift: >`>` (in XML descriptor files use &gt;&gt;)

Right shift: >`>`>` (in XML descriptor files use &gt;&gt;&gt;)

### Relation Operators

`<=, `<, >`, =>` (in XML descriptor files use &lt;=, &lt;, &gt;, =&gt;)

### Equality Operators

`==`, `!=`

### Bitwise Operators

And: `&` (in XML descriptor files use `&amp;`)

Exclusive Or: `^`

Inclusive Or: `|`

### Boolean Operators

And: `&&` (in XML descriptor files use `&amp;&amp;`)

Or: `||`

rapiddwellerScript uses shortcut evaluation like C, C++ and Java: First it evaluates the left hand side of an operation. If the result is completely
determined by the result, it does not evaluate the right hand side.

### Conditional Expression

... ? ... : …, e.g. `a>3 ? 0 : 1`

### Assignment

`qualifiedName = expression`