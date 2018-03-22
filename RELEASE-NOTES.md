
## Release Notes

| version | release date | Scala  | Tabula |
|:--------|:-------------|:------:|:------:|
| v0.3.0  | (unreleased) | 2.12.5 | 0.3.0  |
| v0.2.0  | 2016-12-12   | 2.11.0 | 0.2.0  |
| v0.1.0  | 2015-12-21   | 2.11.0 | 0.1.0  |



### v0.3.0
*(unreleased)*
* coordinated with [Tabula 0.3.0](https://github.com/julianmendez/tabula)
* compiled with Scala 2.12.5
* requires `scala-library-2.12.5.jar`
* its artifact identifiers include the Scala version as suffix (`_2.12`)
* includes more primitive types:
  * Integer
  * List_Integer
  * Decimal
  * List_Decimal
  * Empty
* executes `normalize` extension, if no extension is given
* shows a warning instead of stopping when the normalization finds duplicated identifiers
* includes a `prefix` map to shorten URIs
* uses the Scala collections and changes its traits accordingly
* uses [ScalaTest](http://www.scalatest.org) for unit tests
* build command:

```
$ mvn clean install
```

* release: `tabulas-distribution/target/tabulas-0.3.0.jar`


### v0.2.0
*(2016-12-12)*
* coordinated with [Tabula 0.2.0](https://github.com/julianmendez/tabula)
* compiled with Scala 2.11.0
* requires `scala-library-2.11.0.jar`
* build command:

```
$ mvn clean install
```

* release: `tabulas-distribution/target/tabulas-0.2.0.jar`


### v0.1.0
*(2015-12-21)*
* coordinated with [Tabula 0.1.0](https://github.com/julianmendez/tabula)
* compiled with Scala 2.11.0
* requires `scala-library-2.11.0.jar`
* primitive types:
  * String
  * List_String
  * URI
  * List_URI
* readers (extension names between parentheses):
  * (`simple` / `normalize`) tabula format
  * (`parsecsv`) comma-separated values
  * (`parsecalendar`) calendar
* writers (extension names between parentheses):
  * (`simple` / `normalize`) tabula format
  * (`csv`) comma-separated values
  * (`html`) HTML
  * (`wikitext`) WikiText
  * (`sql`) SQL
* build command:

```
$ mvn clean install
```

* release: `tabulas-distribution/target/tabulas-0.1.0.jar`


