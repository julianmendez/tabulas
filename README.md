# Tabulas
*System to manage human-readable tables using files*


[![Build Status](https://travis-ci.org/julianmendez/tabulas.png?branch=master)](https://travis-ci.org/julianmendez/tabulas)


Tabulas is a system to manage human-readable tables using files. Tabulas is an experimental semi-automatic [Scala](http://www.scala-lang.org/) reimplementation of [Tabula](http://github.com/julianmendez/tabula), which is implemented in Java.
It uses a specific type of file format that is similar to a [Java Properties](http://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-) file, but allows defining the same property for different objects.


## Source code

To clone and compile the project:
```
$ git clone https://github.com/julianmendez/tabulas.git
$ cd tabulas
$ mvn clean install
```
The created executable library, its sources, and its Javadoc will be in `tabulas-distribution/target`.

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

[Julian Mendez](http://lat.inf.tu-dresden.de/~mendez/)


## License

This software is distributed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).


## Release notes

See [release notes](http://github.com/julianmendez/tabulas/blob/master/RELEASE-NOTES.md).


## Format

The Tabula format has *primitive types* and *composite types*. Unless something different is stated in the [release notes](http://github.com/julianmendez/tabula/blob/master/RELEASE-NOTES.md), the primitive types are:
* `String`: any string without any newline ('\n' 0x0A, '\r' 0x0D), and not ending in backslash ('\' 0x5C), neither in blanks ('\t' 0x08, ' ' 0x20)  
* `URI`: any valid Uniform Resource Identifier
* `List_String`: list of space-separated words
* `List_URI`: list of space-separated URIs

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

The *values* can be any Tabula String. The blanks ('\t' 0x08, ' ' 0x20) at the beginning and at the end are removed. To declare a multi-line value, each line must finish with backslash ('\' 0x5C), except the last one. For the sake of simplicity there is no difference between a multi-line value or the concatenation of all those lines. This means that:
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


order = \
 id


new = 
id = arXiv:1412.2223
title = A topological approach to non-Archimedean Mathematics
authors = \
 Vieri_Benci \
 Lorenzo_Luperi_Baglini
web = http://arxiv.org/abs/1412.2223
documents = \
 http://arxiv.org/pdf/1412.2223#pdf \
 http://arxiv.org/ps/1412.2223#ps \
 http://arxiv.org/format/1412.2223#other


new = 
id = arXiv:1412.3313
title = Infinitary stability theory
authors = \
 Sebastien_Vasey
web = http://arxiv.org/abs/1412.3313
documents = \
 http://arxiv.org/pdf/1412.3313#pdf \
 http://arxiv.org/ps/1412.3313#ps \
 http://arxiv.org/format/1412.3313#other


```

## Contact

In case you need more information, please contact @julianmendez .

