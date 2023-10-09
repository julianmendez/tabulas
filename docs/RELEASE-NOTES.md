---
- - version: v1.2.0
  - date: (unreleased)
  - new_features:
    - compiles with Scala 2.13.12
    - can be compiled with Scala 3.3.1
    - requires `scala-library-2.13.12.jar`
  - deprecated_features:
    - use of multiple tables is no longer supported
  - build: mvn clean install
  - release: tabulas-distribution/target/tabulas-1.2.0.jar

- - version: v1.1.0
  - date: (2020-07-20)
  - new_features:
    - compiles with Scala 2.13.3
    - requires `scala-library-2.13.3.jar`
    - its artifact identifiers include the Scala version as suffix `_2.13`
    - new **writers**
    - . `rxyaml` [Rx](http://rx.codesimply.com/) YAML schema
    - . `jsonschema` [JSON Schema](https://json-schema.org/)
  - deprecated_features:
    - use of multiple tables is deprecated and they are no longer supported in newer
      versions
  - bug_fixes:
    - respects the order of the defined URI prefixes when they are expanded
  - build: mvn clean install
  - release: tabulas-distribution/target/tabulas-1.1.0.jar

- - version: v1.0.0
  - date: (2019-06-15)
  - new_features:
    - preserves newline characters in strings and lists
    - uses the colon (`:`) in the default serialization instead of equals sign '='`
      but accepts both symbols in the parser, and there is a renderer for the old
      format
    - requires a field `name` for the type name in every type definition, but accepts
      the previous way of giving the type name
    - compiled with Scala 2.12.8
    - requires `scala-library-2.12.8.jar`
    - its artifact identifiers include the Scala version as suffix `_2.12`
    - executes `normalize` extension, if no extension is given
    - shows a warning instead of stopping when the normalization finds duplicated
      identifiers
    - includes a `prefix` map to shorten URIs
    - change in the Parser and Renderer traits
    - uses the Scala collections and changes its traits accordingly
    - exports only immutable classes
    - uses [ScalaTest](http://www.scalatest.org) for unit tests
    - new **types**
    - . Integer
    - . List_Integer
    - . Decimal
    - . List_Decimal
    - . Empty
    - new **readers**
    - . YAML (autodetected)
    - . JSON (autodetected)
    - . Properties (now autodetected)
    - new **writers**
    - . `yaml` YAML
    - . `json` JSON
    - . `properties` Properties
    - . `oldformat` Tabula format using the equals sign
  - deprecated_features:
    - excludes the experimental calendar parser parsecalendar
  - build: mvn clean install
  - release: tabulas-distribution/target/tabulas-1.0.0.jar

- - version: v0.2.0
  - date: (2016-12-12)
  - new_features:
    - coordinated with [Tabula 0.2.0](https://github.com/julianmendez/tabula)
    - compiled with Scala 2.11.0
    - requires `scala-library-2.11.0.jar`
  - build: mvn clean install
  - release: tabulas-distribution/target/tabulas-0.2.0.jar

- - version: v0.1.0
  - date: (2015-12-21)
  - new_features:
    - coordinated with [Tabula 0.1.0](https://github.com/julianmendez/tabula)
    - compiled with Scala 2.11.0
    - requires `scala-library-2.11.0.jar`
    - new **types**
    - . String
    - . List_String
    - . URI
    - . List_URI
    - new **readers**
    - . `simple` / `normalize` Tabula format
    - . `parsecsv` comma-separated values
    - . `parsecalendar` calendar parser
    - new **writers**
    - . `simple` / `normalize` Tabula format
    - . `csv` comma-separated values
    - . `html` HTML
    - . `wikitext` WikiText
    - . `sql` SQL
  - build: mvn clean install
  - release: tabulas-distribution/target/tabulas-0.1.0.jar

- - schema: RELEASE-NOTES.md.schema.yaml


