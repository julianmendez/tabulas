# [Tabulas](https://julianmendez.github.io/tabulas/)

[![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)][license]
[![Maven Central](https://img.shields.io/maven-central/v/de.tu-dresden.inf.lat.tabulas/tabulas-parent_2.13.svg?label=Maven%20Central)][maven-central]
[![build](https://github.com/julianmendez/tabulas/workflows/Scala%20CI/badge.svg)][build-status]

*System to manage human-readable tables using files*

**Tabulas** is a system to manage human-readable tables using files.
Tabulas is a [Scala][scala] implementation based on the [Tabula][tabula] format.
There are three alternatives to represent the content:
- **Tabula.YAML**, using the [YAML][yaml] format,
- **Tabula.JSON**, using the [JSON][json] format,
- **Tabula.Properties**, using a sort of [Java Properties][java-properties] syntax,
but defining the same property name for multiple objects.

In addition, there are two alternatives to export the metadata as schema:
- **JSON Schema**, for [JSON Schema][json-schema], a vocabulary to annotate and validate JSON
  documents
- **Rx YAML**, for [Rx][rx], schemata tool for JSON/YAML


## Download

- [executable JAR file][executable-jar]
- [The Central Repository][central-repository]
- as dependency:

```xml
<dependency>
  <groupId>de.tu-dresden.inf.lat.tabulas</groupId>
  <artifactId>tabulas-ext_2.13</artifactId>
  <version>1.1.0</version>
</dependency>
```


## Format

The Tabula format is a system that puts constraints on other formats.
It could be viewed as a simplified type system.

The *primitive types* are:

- `String`: any string without any newline (`'\n'` 0x0A, `'\r'` 0x0D), and not ending in backslash (`'\'` 0x5C), neither in blanks (`'\t'` 0x08, `' '` 0x20)
- `URI`: any valid Uniform Resource Identifier
- `Integer`: an integer number (implemented with `BigInteger`)
- `Decimal`: a decimal number (implemented with `BigDecimal`)
- `List_`... (e.g. `List_String`): list of space-separated values, for the types above
- `Empty`: type to ignore any given value

A *composite type* is a structure containing *fields*, each of them of a particular primitive type.
Each *instance* may contain values of the defined fields.

For the sake of clarity, we can compare this to a spreadsheet, with the following associations:
- primitive type: allowed type in the spreadsheet cells
- composite type: first row of the spreadsheet defining the column names
- field: a column, with all its cells of the same type
- instance: a row

This is how types are defined with the Tabulas.YAML format.

The type name can be any Tabula String.
The field name can be any Tabula String that does not contain a colon (`':'` 0x3A), neither an equals sign (`'='` 0x3D), and is not the words `type` or `new`.

Each type is defined as follows:

```yaml

---
- type:
    name: TYPE_NAME
```

where *TYPE_NAME* can be any identifier.

The fields are defined as follows:

```yaml
    def:
    - FIELD_NAME_0:FIELD_TYPE_0
    - FIELD_NAME_1:FIELD_TYPE_1
      ...
    - FIELD_NAME_n:FIELD_TYPE_n
```

where each *FIELD_NAME* can be any identifier,
and each *FIELD_TYPE* can be any of the primitive types.
No space must be left before or after the colon.
For example, it is `FIELD_NAME_0:FIELD_TYPE_0` and not `FIELD_NAME_0: FIELD_TYPE_0`.

The URIs can be shortened by using prefixes.
The prefixes are URIs themselves without colons, because the colon (`:`) is used to define the association.

```yaml
    prefix:
    - PREFIX_0:URI_0
    - PREFIX_1:URI_1
    - ...
    - PREFIX_n:URI_n
```

No space must be left before or after the colon.
They are applied using the declaration order during parsing and serialization.

Although the serialization shortens every possible URI using the prefixes, it is possible to expand all of them by adding the empty prefix with an empty value, i.e. a colon (`:`) alone, and it has to be the first prefix.
This could be useful to rename the prefixes.

The order in which the instances are shown is defined as follows:

```yaml
    order:
    - ('-'|'+')FIELD_NAME_a_0
    - ('-'|'+')FIELD_NAME_a_1
      ...
    - ('-'|'+')FIELD_NAME_a_k
```

where the `+` and the `-` are used to denote whether the reverse order should be used.
For example:

```yaml
    order:
    - +id
    - -author
```

orders the instances by `id` (ascending) and then by author (descending).

The instances come just after the type definition, with the following syntax:

```yaml
- FIELD_NAME_0: VALUE_0
  FIELD_NAME_1: VALUE_1
  ...
  FIELD_NAME_n: VALUE_n
```

where each *FIELD_NAME* is one of the already declared field names in the type and each *VALUE* contains a String according to the field type.

The *values* can be any Tabula String.
The blanks (`'\t'` 0x08, `' '` 0x20) at the beginning and at the end are removed.
To declare a multi-line value, each line must finish with backslash (`'\'` 0x5C), except the last one.

The formatter normalizes the values and present them differently according to the declared type.
For example, the values of fields with type `List_`... (e.g. `List_String`) will be presented as multi-line values.


## Example

This is an example of a library file.
Each book record contains an identifier (`id`), a title (`title`), the authors (`authors`), a link to the abstract on the web (`web`), and a list of links to the documents (`documents`).
The entries are ordered by identifier.

```yaml

---
- type:
    name: record
    def:
    - id:String
    - title:String
    - authors:List_String
    - web:URI
    - documents:List_URI
    prefix:
    - arxiv:https://arxiv.org/
    order:
    - +id

- id: arXiv:1412.2223
  title: A topological approach to non-Archimedean Mathematics
  authors:
  - Vieri Benci
  - Lorenzo Luperi Baglini
  web: https://arxiv.org/abs/1412.2223
  documents:
  - https://arxiv.org/pdf/1412.2223#pdf
  - https://arxiv.org/ps/1412.2223#ps
  - https://arxiv.org/format/1412.2223#other

- id: arXiv:1412.3313
  title: Infinitary stability theory
  authors:
  - Sebastien Vasey
  web: https://arxiv.org/abs/1412.3313
  documents:
  - https://arxiv.org/pdf/1412.3313#pdf
  - https://arxiv.org/ps/1412.3313#ps
  - https://arxiv.org/format/1412.3313#other

```

The unit tests include an example like [this one][miniexample-properties].

For example, the [MainSpec][main-spec] class does the following steps:

- read the [example file][example-properties]
- add a new field `numberOfAuthors`
- add to each record the number of authors
- compare the [expected result][updated-example-properties]


## Other formats

This project also includes some converters from and to other formats.
Every deserializer (parser) and serializer (renderer) is registered as an extension.
Some serializers and some deserializers cannot map completely the content of a Tabula file.

| serializer   | stores metadata   | stores entries |
|:-------------|:------------------|:---------------|
| YAML         | yes               | yes            |
| JSON         | yes               | yes            |
| JSON Schema  | yes               | no             |
| Rx YAML      | yes               | no             |
| HTML         | no                | yes            |
| Wikitext     | no                | yes            |
| CSV          | no                | yes            |
| SQL          | no                | yes            |

([Wikitext][wikitext]: is a wiki markup language)

| deserializer | requires metadata |
|:-------------|:------------------|
| YAML         | yes               |
| JSON         | yes               |
| CSV          | no                |

The given example as Tabula.Properties:

```properties


# simple format 1.0.0

type:
 name: record
 def: \
  id:String \
  title:String \
  authors:List_String \
  web:URI \
  documents:List_URI
 prefix: \
  arxiv:https://arxiv.org/
 order: \
  +id

new:
 id: arXiv:1412.2223
 title: A topological approach to non-Archimedean Mathematics
 authors: \
  Vieri Benci \
  Lorenzo Luperi Baglini
 web: &arxiv;abs/1412.2223
 documents: \
  &arxiv;pdf/1412.2223#pdf \
  &arxiv;ps/1412.2223#ps \
  &arxiv;format/1412.2223#other

new:
 id: arXiv:1412.3313
 title: Infinitary stability theory
 authors: \
  Sebastien Vasey
 web: &arxiv;abs/1412.3313
 documents: \
  &arxiv;pdf/1412.3313#pdf \
  &arxiv;ps/1412.3313#ps \
  &arxiv;format/1412.3313#other

```

The unit tests also include the [previous example][miniexample-yaml].

Please note that there should be no spaces in the elements of the `def` section.
For example, the definition is `id:String` and not `id: String`.

A YAML file can be easily converted to a JSON file using a [Python][python] script like
[yaml_to_json.py][yaml-to-json].


## Extensions

The command line application can be used to execute the different readers and writers.
They are implemented as *extensions*.
Each extension registers at the beginning of the execution and is available to be executed from the command line.

The following example contains some extensions listed by the application, when no parameters are given.

- `yaml` *(input)* *(output)*: create a Tabula.YAML file
- `json` *(input)* *(output)*: create a Tabula.JSON file
- `properties` *(input)* *(output)*: create a Tabula.Properties file
- `oldformat` *(input)* *(output)*: create an old Tabula.Properties file, i.e. using the equals sign instead of colon

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
This executable JAR file requires the [Scala library][scala-library] in the same directory.
The required version is shown in the release notes.

To compile the project offline, first download the dependencies:

```
$ mvn dependency:go-offline
```

and once offline, use:

```
$ mvn --offline clean install
```

The bundles uploaded to [Sonatype][sonatype] are created with:

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

[Julian Alfredo Mendez][author]


## License

This software is distributed under the [Apache License Version 2.0][license].


## Release notes

See [release notes][release-notes].


## Contact

In case you need more information, please contact [julianmendez][author].

[author]: https://julianmendez.github.io
[central-repository]: https://repo1.maven.org/maven2/de/tu-dresden/inf/lat/tabulas/
[license]: https://www.apache.org/licenses/LICENSE-2.0.txt
[maven-central]: https://search.maven.org/artifact/de.tu-dresden.inf.lat.tabulas/tabulas-ext_2.13
[build-status]: https://github.com/julianmendez/tabulas/actions
[sonatype]: https://oss.sonatype.org
[executable-jar]: https://sourceforge.net/projects/latitude/files/tabulas/1.1.0/tabulas-1.1.0.jar/download
[release-notes]: https://julianmendez.github.io/tabulas/RELEASE-NOTES.html
[scala-library]: https://search.maven.org/#search|gav|1|g%3A%22org.scala-lang%22%20AND%20a%3A%22scala-library%22
[yaml-to-json]: https://github.com/julianmendez/tabulas/blob/master/tabulas-ext/src/main/python/yaml_to_json.py
[example-properties]: https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/resources/core/example.tab.properties
[updated-example-properties]: https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/resources/core/example-modified.tab.properties
[miniexample-yaml]: https://github.com/julianmendez/tabulas/blob/master/tabulas-ext/src/test/resources/ext/miniexample.tab.yaml
[miniexample-properties]: https://github.com/julianmendez/tabulas/blob/master/tabulas-ext/src/test/resources/ext/miniexample.tab.properties
[main-spec]: https://github.com/julianmendez/tabulas/blob/master/tabulas-core/src/test/scala/de/tudresden/inf/lat/tabulas/main/MainSpec.scala
[tabula]: https://github.com/julianmendez/tabula
[wikitext]: https://www.mediawiki.org/wiki/Specs/wikitext/1.0.0
[yaml]: https://yaml.org
[json]: https://json.org
[json-schema]: https://json-schema.org
[rx]: https://rx.codesimply.com
[java-properties]: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Properties.html#load-java.io.Reader-
[scala]: https://www.scala-lang.org
[python]: https://www.python.org


