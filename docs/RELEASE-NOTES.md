---

- type :
    name : release_notes
    def :
    - version:String
    - date:String
    - new_features:List_String
    - bug_fixes:List_String
    - new_types:List_String
    - new_readers:List_String
    - new_writers:List_String
    - build:String
    - release:String
    prefix :

    order :

- version : v1.1.0
  date : (*unreleased*)
  new_features :
  - compiled with Scala 2.13.2
  - requires `scala-library-2.13.2.jar`
  - its artifact identifiers include the Scala version as suffix (`_2.13`)
  bug_fixes :
  - respects the order of the defined URI prefixes when they are expanded
  new_writers :
  - (`rxyaml`) [Rx](http://rx.codesimply.com/) YAML schema
  - (`jsonschema`) [JSON Schema](https://json-schema.org/)
  build : (`$ mvn clean install`)
  release : (`tabulas-distribution/target/tabulas-1.1.0.jar`)

- version : v1.0.0
  date : (*2019-06-15*)
  new_features :
  - preserves newline characters in strings and lists
  - uses the colon (`':'`) in the default serialization instead of equals sign (`'='`), but accepts both symbols in the parser, and there is a renderer for the old format
  - requires a field `name` for the type name in every type definition, but accepts the previous way of giving the type name
  - compiled with Scala 2.12.8
  - requires `scala-library-2.12.8.jar`
  - its artifact identifiers include the Scala version as suffix (`_2.12`)
  - executes `normalize` extension, if no extension is given
  - shows a warning instead of stopping when the normalization finds duplicated identifiers
  - includes a `prefix` map to shorten URIs
  - change in the Parser and Renderer traits
  - uses the Scala collections and changes its traits accordingly
  - exports only immutable classes
  - uses [ScalaTest](http://www.scalatest.org) for unit tests
  - excludes the experimental calendar parser (`parsecalendar`)
  new_types :
  - Integer
  - List_Integer
  - Decimal
  - List_Decimal
  - Empty
  new_readers :
  - YAML (autodetected)
  - JSON (autodetected)
  - Properties (now autodetected)
  new_writers :
  - (`yaml`) YAML
  - (`json`) JSON
  - (`properties`) Properties
  - (`oldformat`) Tabula format using the equals sign
  build : (`$ mvn clean install`)
  release : (`tabulas-distribution/target/tabulas-1.0.0.jar`)

- version : v0.2.0
  date : (*2016-12-12*)
  new_features :
  - coordinated with [Tabula 0.2.0](https://github.com/julianmendez/tabula)
  - compiled with Scala 2.11.0
  - requires `scala-library-2.11.0.jar`
  build : (`$ mvn clean install`)
  release : (`tabulas-distribution/target/tabulas-0.2.0.jar`)

- version : v0.1.0
  date : (*2015-12-21*)
  new_features :
  - coordinated with [Tabula 0.1.0](https://github.com/julianmendez/tabula)
  - compiled with Scala 2.11.0
  - requires `scala-library-2.11.0.jar`
  new_types :
  - String
  - List_String
  - URI
  - List_URI
  new_readers :
  - (`simple` / `normalize`) Tabula format
  - (`parsecsv`) comma-separated values
  - (`parsecalendar`) calendar parser
  new_writers :
  - (`simple` / `normalize`) Tabula format
  - (`csv`) comma-separated values
  - (`html`) HTML
  - (`wikitext`) WikiText
  - (`sql`) SQL
  build : (`$ mvn clean install`)
  release : (`tabulas-distribution/target/tabulas-0.1.0.jar`)


