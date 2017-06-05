# [Tabulas](https://julianmendez.github.io/tabulas/)
*System to manage human-readable tables using files*


[![Build Status](https://travis-ci.org/julianmendez/tabulas.png?branch=master)](https://travis-ci.org/julianmendez/tabulas)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.tu-dresden.inf.lat.tabulas/tabulas-parent/badge.svg)](https://search.maven.org/#search|ga|1|g%3A%22de.tu-dresden.inf.lat.tabulas%22)


Tabulas is a system to manage human-readable tables using files. Tabulas is an experimental semi-automatic [Scala](https://www.scala-lang.org/) reimplementation of [Tabula](https://github.com/julianmendez/tabula), which is implemented in Java.
It uses a specific type of file format that is similar to a [Java Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-) file, but allows defining the same property for different objects.


## Download

* [executable JAR file](https://sourceforge.net/projects/latitude/files/tabulas/0.2.0/tabulas-0.2.0.jar/download)
* [The Central Repository](https://repo1.maven.org/maven2/de/tu-dresden/inf/lat/tabulas/)
* as dependency:

```xml
<dependency>
  <groupId>de.tu-dresden.inf.lat.tabulas</groupId>
  <artifactId>tabulas-ext</artifactId>
  <version>0.2.0</version>
</dependency>
```


## Format

The Tabula format has *primitive types* and *composite types*. Unless something different is stated in the [release notes](https://github.com/julianmendez/tabula/blob/master/RELEASE-NOTES.md), the primitive types are:

* `String`: any string without any newline (`'\n'` 0x0A, `'\r'` 0x0D), and not ending in backslash (`'\'` 0x5C), neither in blanks (`'\t'` 0x08, `' '` 0x20)  
* `URI`: any valid Uniform Resource Identifier
* `Integer`: an integer number (`BigInteger`)
* `Decimal`: a decimal number (`BigDecimal`)
* `List_`... (e.g. `List_String`): list of space-separated values, for the types above
* `Empty`: type that ignores any given value

With this format it is possible to define one or many composite *types*. Each type is defined by its *fields*. The *instances* of each type are listed just after the type definition.
The name of a type or field can be any *identifier*. A identifier is a word that is not any of the reserved words: `type`, `def`, `new`, `id`.
Instances can be identified by the field `id`.

Each type is defined as follows:

```properties
type = TYPE_NAME
```

where *TYPE_NAME* can be any identifier.

Each type has its *fields*, defined as follow:

```properties
def = \
 FIELD_NAME_0:FIELD_TYPE_0 \
 FIELD_NAME_1:FIELD_TYPE_1 \
...
 FIELD_NAME_n:FIELD_TYPE_n
```

where each *FIELD_NAME* can be any identifier,
and each *FIELD_TYPE* can be any of the primitive types.

The URIs can be shortened by using prefixes. The prefixes are URIs themselves without colons, because the colon (`:`) is used to define the association. 

```properties
prefix = \
 PREFIX_0:URI_0 \
 PREFIX_1:URI_1 \
 ...
 PREFIX_n:URI_n
```

The prefixes are sorted by alphabetical order. They are applied using that order during parsing and serialization. Although the serialization shortens every possible URI using the prefixes, it is possible to expand all of them by adding the empty prefix with an empty value, i.e. a colon (`:`) alone. This could be useful to rename the prefixes.

The order in which the instances are shown is defined as follows:

```properties
order = \
 [-]FIELD_NAME_a_0 \
 [-]FIELD_NAME_a_1 \
 ...
 [-]FIELD_NAME_a_k
```

where the `-` is optional and used to denote reverse order. For example:

```properties
order = \
 id \
 -author
``` 

orders the instances by `id` (ascending) and then by author (descending).
 
The instances come just after the type definition, with the following syntax:

```properties
new =
FIELD_NAME_0 = VALUE_0
FIELD_NAME_1 = VALUE_1
...
FIELD_NAME_n = VALUE_n
```

where each *FIELD_NAME* is one of the already declared field names in the type and each *VALUE* contains a String accoding to the field type.

The *values* can be any Tabula String. The blanks (`'\t'` 0x08, `' '` 0x20) at the beginning and at the end are removed. To declare a multi-line value, each line must finish with backslash (`'\'` 0x5C), except the last one. For the sake of simplicity there is no difference between a multi-line value or the concatenation of all those lines. This means that:

```properties
field_name = \
 a \
 b \
 c
```

is the same as

```properties
field_name = a b c
```

However, the format will normalize and present them differently according to the declared type. Thus, the values of fields with type `List_String` and `List_URI` will be presented as multi-line values.


## Example

This is an example of a library file. Each book record contains an identifier (`id`), a title (`title`), the authors (`authors`), a link to the abstract on the web (`web`), and a list of links to the documents (`documents`). This file is ordered by identifier.


```properties
# simple format 1.0.0


type = record 


def = \
 id:String \
 title:String \
 authors:List_String \
 web:URI \
 documents:List_URI


prefix = \
 arxiv:https://arxiv.org/


order = \
 id


new = 
id = arXiv:1412.2223
title = A topological approach to non-Archimedean Mathematics
authors = \
 Vieri_Benci \
 Lorenzo_Luperi_Baglini
web = https://arxiv.org/abs/1412.2223
documents = \
 https://arxiv.org/pdf/1412.2223#pdf \
 https://arxiv.org/ps/1412.2223#ps \
 https://arxiv.org/format/1412.2223#other


new = 
id = arXiv:1412.3313
title = Infinitary stability theory
authors = \
 Sebastien_Vasey
web = &arxiv;abs/1412.3313
documents = \
 &arxiv;pdf/1412.3313#pdf \
 &arxiv;ps/1412.3313#ps \
 &arxiv;format/1412.3313#other


```

An example like this one is used for the unit tests.

For example, the [MainTest](https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/scala/de/tudresden/inf/lat/tabulas/main/MainTest.scala) class does the following steps:

* read the [example file](https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/resources/example.properties)
* add a new field `numberOfAuthors`
* add to each record the number of authors 
* compare the [expected result](https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/resources/example-modified.properties)

This [Bash script](https://github.com/julianmendez/tabulas/blob/master/docs/examples/tabulas.sh.txt) shows how to start Tabulas from the command line.


## Source code

To clone and compile the project:

```
$ git clone https://github.com/julianmendez/tabulas.git
$ cd tabulas
$ mvn clean install
```

The created executable library, its sources, and its Javadoc will be in `tabulas-distribution/target`.
This executable JAR file requires the [Scala library](https://search.maven.org/#search|gav|1|g%3A%22org.scala-lang%22%20AND%20a%3A%22scala-library%22) in the same directory. The required version is shown in the [release notes](https://github.com/julianmendez/tabulas/blob/master/RELEASE-NOTES.md).

To compile the project offline, first download the dependencies:

```
$ mvn dependency:go-offline
```

and once offline, use:

```
$ mvn --offline clean install
```

The bundles uploaded to [Sonatype](https://oss.sonatype.org/) are created with:

```
$ mvn clean install -DperformRelease=true
```

and then on each module:

```
$ cd target
$ jar -cf bundle.jar tabulas-*
```

and on the main directory:

```
$ cd target
$ jar -cf bundle.jar tabulas-parent-*
```

The version number is updated with:

```
$ mvn versions:set -DnewVersion=NEW_VERSION
```

where *NEW_VERSION* is the new version.


## Author

[Julian Mendez](https://lat.inf.tu-dresden.de/~mendez/)


## License

This software is distributed under the [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).


## Release notes

See [release notes](https://github.com/julianmendez/tabulas/blob/master/RELEASE-NOTES.md).


## Contact

In case you need more information, please contact @julianmendez .

