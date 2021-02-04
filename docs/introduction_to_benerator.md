# Goals and Features <br />Introduction to Benerator

## Goals

The core goals of Benerator are

* Generation of data that satisfies complex data validity requirements

* Efficient generation of large data volumes

* Early applicability in projects

* Little maintenance effort with ongoing implementation through configuration by exception

* Wide and easy customizability

* Applicability by non-developers

* Intuitive data definition format

* Satisfying stochastic requirements on data

* Extraction and anonymization of production data

* Supporting distributed and heterogeneous applications

* Establishing a common data generation platform for different business domains and software systems

## Features

### Data Synthesization

Performance test data can be completely synthesized. A basic setup can be imported e.g. from DbUnit files, CSV files and fixed column width files. A
descriptor file configures how imported data should be processed and adds completely synthesized data. The processed or generated data finally is
stored in the system under test.

![](assets/grafik1.png)

### Production Data Anonymization

Production data can be easily extracted from production systems. Tables can be imported unmodified, filtered, anonymized and converted.

![](assets/grafik2.png)

## State of the Benerator

Benerator is developed and continuously extended and improved since June 2006\. Benerator is mainly used and tested best for data file and database
data generation, for these applications Benerator should help you for almost all your data generation needs out of the box - and extending Benerator
for specific needs is easy.

XML-Schema, on the other hand, allows for an extraordinarily wide range of features. Benerator's XML support is limited to features that are useful
for generating XML data structures (no mixed content) and does not yet support all variants possible with XML schema. The elements `<unique>`, `<key>`
and `<keyRef>` cannot be handled automatically, but require manual configuration. The following features are not yet implemented: `<group>`
, `<import>`, `<all>` and `<sequence>` with minCount != 1 or maxCount != 1\. If you need support for some of these, please contact us.

## Building Blocks

![](assets/grafik3.png)

## Database Support

All common SQL data types are supported.

Benerator was tested with and provides examples for

* Oracle 19c (thin driver)

* DB2

* MS SQL Server

* MySQL 5

* PostgreSQL 12

* HSQL 2.x

* H2 1.2

* Derby 10.3

* Firebird