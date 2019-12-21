# [Tabulas](https://julianmendez.github.io/tabulas/)

[![build](https://travis-ci.org/julianmendez/tabulas.png?branch=master)](https://travis-ci.org/julianmendez/tabulas)
[![maven central](https://maven-badges.herokuapp.com/maven-central/de.tu-dresden.inf.lat.tabulas/tabulas-parent_2.12/badge.svg)](https://search.maven.org/#search|ga|1|g%3A%22de.tu-dresden.inf.lat.tabulas%22)
[![license](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

*System to manage human-readable tables using files*

**Tabulas** is a system to manage human-readable tables using files.
Tabulas is a [Scala](https://www.scala-lang.org/) implementation based on the [Tabula](https://github.com/julianmendez/tabula) format.
There are three alternatives to represent the content:
- **Tabula/Properties**, which is a type of file format that is similar to a [Java Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-) file,
but allows defining the same property for different objects.
- **Tabula/JSON**, using the [JSON](https://json.org) format,
- **Tabula/YAML**, using the [YAML](https://yaml.org) format.


## Download

- [executable JAR file](https://sourceforge.net/projects/latitude/files/tabulas/1.0.0/tabulas-1.0.0.jar/download)
- [The Central Repository](https://repo1.maven.org/maven2/de/tu-dresden/inf/lat/tabulas/)
- as dependency:

```xml
<dependency>
  <groupId>de.tu-dresden.inf.lat.tabulas</groupId>
  <artifactId>tabulas-ext_2.12</artifactId>
  <version>1.0.0</version>
</dependency>
```


## Format

This describes the project as it is in the `master` branch.

In this section, the Tabula format is explained using the Tabula/Properties representation.

The Tabula format has *primitive types* and *composite types*.
Unless something different is stated in the release notes, the primitive types are:

- `String`: any string without any newline (`'\n'` 0x0A, `'\r'` 0x0D), and not ending in backslash (`'\'` 0x5C), neither in blanks (`'\t'` 0x08, `' '` 0x20)
- `URI`: any valid Uniform Resource Identifier
- `Integer`: an integer number (`BigInteger`)
- `Decimal`: a decimal number (`BigDecimal`)
- `List_`... (e.g. `List_String`): list of space-separated values, for the types above
- `Empty`: type that ignores any given value

With this format it is possible to define one or many composite *types*. Each type is defined by its *fields*. The *instances* of each type are listed just after the type definition. The type name can be any Tabula String.
The field name can be any Tabula String that does not contain a colon (`':'` 0x3A) neither an equals sign (`'='` 0x3D), and that is not the words `type` or `new`.

Each type is defined as follows:

```properties
type :
 name : TYPE_NAME
```

where *TYPE_NAME* can be any identifier.

Each type has its *fields*, defined as follow:

```properties
 def : \
  FIELD_NAME_0:FIELD_TYPE_0 \
  FIELD_NAME_1:FIELD_TYPE_1 \
  ...
  FIELD_NAME_n:FIELD_TYPE_n
```

where each *FIELD_NAME* can be any identifier,
and each *FIELD_TYPE* can be any of the primitive types.
No space must be left before or after the colon. For example, it is `FIELD_NAME_0:FIELD_TYPE_0` and not `FIELD_NAME_0: FIELD_TYPE_0`.

The URIs can be shortened by using prefixes. The prefixes are URIs themselves without colons, because the colon (`:`) is used to define the association.

```properties
 prefix : \
  PREFIX_0:URI_0 \
  PREFIX_1:URI_1 \
  ...
  PREFIX_n:URI_n
```

No space must be left before or after the colon.
They are applied using the declaration order during parsing and serialization. Although the serialization shortens every possible URI using the prefixes, it is possible to expand all of them by adding the empty prefix with an empty value, i.e. a colon (`:`) alone, and it has to be the first prefix. This could be useful to rename the prefixes.

The order in which the instances are shown is defined as follows:

```properties
 order : \
  ['-'|'+']FIELD_NAME_a_0 \
  ['-'|'+']FIELD_NAME_a_1 \
  ...
  ['-'|'+']FIELD_NAME_a_k
```

where the `+` and the `-` are used to denote whether the reverse order should be used. For example:

```properties
order : \
 +id \
 -author
```

orders the instances by `id` (ascending) and then by author (descending).

The instances come just after the type definition, with the following syntax:

```properties
new :
 FIELD_NAME_0 : VALUE_0
 FIELD_NAME_1 : VALUE_1
 ...
 FIELD_NAME_n : VALUE_n
```

where each *FIELD_NAME* is one of the already declared field names in the type and each *VALUE* contains a String accoding to the field type.

The *values* can be any Tabula String. The blanks (`'\t'` 0x08, `' '` 0x20) at the beginning and at the end are removed. To declare a multi-line value, each line must finish with backslash (`'\'` 0x5C), except the last one.

The formatter normalizes the values and present them differently according to the declared type. For example, the values of fields with type `List_`... (e.g. `List_String`) will be presented as multi-line values.


## Example

This is an example of a library file. Each book record contains an identifier (`id`), a title (`title`), the authors (`authors`), a link to the abstract on the web (`web`), and a list of links to the documents (`documents`). This file is ordered by identifier.

```properties


# simple format 1.0.0

type :
 name : record
 def : \
  id:String \
  title:String \
  authors:List_String \
  web:URI \
  documents:List_URI
 prefix : \
  arxiv:https://arxiv.org/
 order : \
  +id

new :
 id : arXiv:1412.2223
 title : A topological approach to non-Archimedean Mathematics
 authors : \
  Vieri Benci \
  Lorenzo Luperi Baglini
 web : https://arxiv.org/abs/1412.2223
 documents : \
  https://arxiv.org/pdf/1412.2223#pdf \
  https://arxiv.org/ps/1412.2223#ps \
  https://arxiv.org/format/1412.2223#other

new :
 id : arXiv:1412.3313
 title : Infinitary stability theory
 authors : Sebastien Vasey
 web : &arxiv;abs/1412.3313
 documents : \
  &arxiv;pdf/1412.3313#pdf \
  &arxiv;ps/1412.3313#ps \
  &arxiv;format/1412.3313#other

```

The unit tests include an example like [this one](https://github.com/julianmendez/tabulas/blob/master/tabulas-ext/src/test/resources/ext/miniexample.properties).

For example, the [MainTest](https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/scala/de/tudresden/inf/lat/tabulas/main/MainTest.scala) class does the following steps:

- read the [example file](https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/resources/core/example.properties)
- add a new field `numberOfAuthors`
- add to each record the number of authors
- compare the [expected result](https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/resources/core/example-modified.properties)


## Other formats

This project also includes some converters from and to other formats.
Every deserializer (parser) and serializer (renderer) is registered as an extension.
Some serializers and some deserializers cannot map completely the content of a Tabula file.

| serializer   | stores metadata   | multiple tables |
|:-------------|:------------------|:----------------|
| YAML         | yes               | yes             |
| JSON         | yes               | yes             |
| HTML         | no                | yes             |
| [Wikitext](https://www.mediawiki.org/wiki/Specs/wikitext/1.0.0) | no | yes |
| CSV          | no                | no              |
| SQL          | no                | no              |

| deserializer | requires metadata | multiple tables |
|:-------------|:------------------|:----------------|
| YAML         | yes               | yes             |
| JSON         | yes               | yes             |
| CSV          | no                | no              |

The given example (as Tabula/Properties) is converted to a YAML file (i.e. Tabula/YAML) as follows:
```yaml

---

- type :
    name : record
    def :
    - id:String
    - title:String
    - authors:List_String
    - web:URI
    - documents:List_URI
    prefix :
    - arxiv:https://arxiv.org/
    order :
    - +id

- id : arXiv:1412.2223
  title : A topological approach to non-Archimedean Mathematics
  authors :
  - Vieri Benci
  - Lorenzo Luperi Baglini
  web : https://arxiv.org/abs/1412.2223
  documents :
  - https://arxiv.org/pdf/1412.2223#pdf
  - https://arxiv.org/ps/1412.2223#ps
  - https://arxiv.org/format/1412.2223#other

- id : arXiv:1412.3313
  title : Infinitary stability theory
  authors :
  - Sebastien Vasey
  web : https://arxiv.org/abs/1412.3313
  documents :
  - https://arxiv.org/pdf/1412.3313#pdf
  - https://arxiv.org/ps/1412.3313#ps
  - https://arxiv.org/format/1412.3313#other

```

The unit tests also include the [previous example](https://github.com/julianmendez/tabulas/blob/master/tabulas-ext/src/test/resources/ext/miniexample.yaml).

Please note that there should be no spaces in the elements of the `def` section. For example, the definition is `id:String` and not `id : String`.

A YAML file can be easily converted to a JSON file using a [Python](https://www.python.org) script like
[yaml_to_json.py](https://github.com/julianmendez/tabulas/blob/master/tabulas-ext/src/main/python/yaml_to_json.py).


## Extensions

The command line application can be used to execute the different readers and writers.
They are implemented as *extensions*.
Each extension registers at the beginning of the execution and is available to be executed from the command line.

The following example contains some of the extensions listed by the application, when no parameters are given.

- `yaml` *(input)* *(output)* : create a Tabula/YAML file
- `json` *(input)* *(output)* : create a Tabula/JSON file
- `properties` *(input)* *(output)* : create a Tabula/Properties file
- `oldformat` *(input)* *(output)* : create an old Tabula/Properties file, i.e. using the equals sign instead of colon

The command line application can be executed with:

`java -jar` *(jarname)* *(extension)* *(input)* *(output)*

The executable JAR file is available at the link provided in the *Download* section.
If the project is build from its source code, the executable JAR file will be available in the location indicated by the *release* property of the release notes.


## Source code

To clone and compile the project:

```
$ git clone https://github.com/julianmendez/tabulas.git
$ cd tabulas
$ mvn clean install
```

The created executable library, its sources, and its Javadoc will be in `tabulas-distribution/target`.
This executable JAR file requires the [Scala library](https://search.maven.org/#search|gav|1|g%3A%22org.scala-lang%22%20AND%20a%3A%22scala-library%22) in the same directory. The required version is shown in the release notes.

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
$ jar -cf bundle.jar tabulas-parent*
```

The version number is updated with:

```
$ mvn versions:set -DnewVersion=NEW_VERSION
```

where *NEW_VERSION* is the new version.


## Author

[Julian Mendez](https://julianmendez.github.io)


## License

This software is distributed under the [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).


## Release notes

See [release notes](https://julianmendez.github.io/tabulas/RELEASE-NOTES.html).


## Contact

In case you need more information, please contact @julianmendez .


