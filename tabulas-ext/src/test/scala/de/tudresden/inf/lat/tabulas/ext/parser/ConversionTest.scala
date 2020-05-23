package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{FileReader, StringWriter}
import java.net.URL
import java.nio.file.{Files, Paths}

import de.tudresden.inf.lat.tabulas.ext.renderer.{JsonRenderer, JsonSchemaRenderer, RxYamlRenderer, YamlRenderer}
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters.CollectionHasAsScala

/** This is a test of conversion.
  */
class ConversionTest extends AnyFunSuite {

  final val ExtPrefix = "ext/"

  final val InputFileName0: String = ExtPrefix + "example.tab.properties"
  final val ExpectedOutputFileName0: String = ExtPrefix + "example-expected.tab.properties"

  final val InputFileName1: String = ExtPrefix + "example.tab.properties"
  final val ExpectedOutputFileName1: String = ExtPrefix + "example.tab.json"

  final val InputFileName2: String = ExtPrefix + "multiple_tables.tab.properties"
  final val ExpectedOutputFileName2: String = ExtPrefix + "multiple_tables.tab.json"

  final val InputFileName3: String = ExtPrefix + "miniexample.tab.properties"
  final val ExpectedOutputFileName3: String = ExtPrefix + "miniexample.tab.json"

  final val InputFileName4: String = ExtPrefix + "example.tab.json"
  final val ExpectedOutputFileName4: String = ExtPrefix + "example-expected.tab.properties"

  final val InputFileName5: String = ExtPrefix + "multiple_tables.tab.json"
  final val ExpectedOutputFileName5: String = ExtPrefix + "multiple_tables-expected.tab.properties"

  final val InputFileName6: String = ExtPrefix + "multiple_tables_2.tab.json"
  final val ExpectedOutputFileName6: String = ExtPrefix + "multiple_tables-expected.tab.properties"

  final val InputFileName7: String = ExtPrefix + "example.tab.properties"
  final val ExpectedOutputFileName7: String = ExtPrefix + "example.tab.yaml"

  final val InputFileName8: String = ExtPrefix + "miniexample.tab.properties"
  final val ExpectedOutputFileName8: String = ExtPrefix + "miniexample.tab.yaml"

  final val InputFileName9: String = ExtPrefix + "example.tab.yaml"
  final val ExpectedOutputFileName9: String = ExtPrefix + "example-expected.tab.properties"

  final val InputFileName10: String = ExtPrefix + "multiple_tables.tab.yaml"
  final val ExpectedOutputFileName10: String = ExtPrefix + "multiple_tables-expected.tab.properties"

  final val InputFileName11: String = ExtPrefix + "example.tab.yaml"
  final val ExpectedOutputFileName11: String = ExtPrefix + "example-expected.rx.yaml"

  final val InputFileName12: String = ExtPrefix + "multiple_tables.tab.yaml"
  final val ExpectedOutputFileName12: String = ExtPrefix + "multiple_tables-expected.rx.yaml"

  final val InputFileName13: String = ExtPrefix + "example.tab.yaml"
  final val ExpectedOutputFileName13: String = ExtPrefix + "example-expected.schema.json"


  final val NewLine: String = "\n"

  def getFileReader(inputFileName: String): FileReader = {
    new FileReader(getPath(inputFileName).getFile)
  }

  def readFile(fileName: String): String = {
    val path = Paths.get(getPath(fileName).toURI)
    val result = Files.readAllLines(path).asScala.mkString(NewLine) + NewLine
    result
  }

  def getPath(fileName: String): URL = {
    getClass.getClassLoader.getResource(fileName)
  }

  test("normalization") {
    val tableMap = SimpleFormatParser().parse(getFileReader(InputFileName0)).get
    val expectedResult = readFile(ExpectedOutputFileName0)
    val writer = new StringWriter()
    SimpleFormatRenderer().render(writer, tableMap)
    val obtainedResult = writer.toString
    assert(obtainedResult === expectedResult)
  }

  test("rendering JSON") {
    Seq(
      (InputFileName1, ExpectedOutputFileName1),
      (InputFileName2, ExpectedOutputFileName2),
      (InputFileName3, ExpectedOutputFileName3)
    ).foreach(pair => {
      val tableMap = SimpleFormatParser().parse(getFileReader(pair._1)).get
      val expectedResult = readFile(pair._2)
      val writer = new StringWriter()
      JsonRenderer().render(writer, tableMap)
      val obtainedResult = writer.toString
      assert(obtainedResult === expectedResult)
    })
  }

  test("parsing JSON") {
    Seq(
      (InputFileName4, ExpectedOutputFileName4),
      (InputFileName5, ExpectedOutputFileName5),
      (InputFileName6, ExpectedOutputFileName6)
    ).foreach(pair => {
      val tableMap = JsonParser().parse(getFileReader(pair._1)).get
      val expectedResult = readFile(pair._2)
      val writer = new StringWriter()
      SimpleFormatRenderer().render(writer, tableMap)
      val obtainedResult = writer.toString
      assert(obtainedResult === expectedResult)
    })
  }

  test("rendering YAML") {
    Seq(
      (InputFileName7, ExpectedOutputFileName7),
      (InputFileName8, ExpectedOutputFileName8)
    ).foreach(pair => {
      val tableMap = SimpleFormatParser().parse(getFileReader(pair._1)).get
      val expectedResult = readFile(pair._2)
      val writer = new StringWriter()
      YamlRenderer().render(writer, tableMap)
      val obtainedResult = writer.toString
      assert(obtainedResult === expectedResult)
    })
  }

  test("parsing YAML") {
    Seq(
      (InputFileName9, ExpectedOutputFileName9),
      (InputFileName10, ExpectedOutputFileName10)
    ).foreach(pair => {
      val tableMap = YamlParser().parse(getFileReader(pair._1)).get
      val expectedResult = readFile(pair._2)
      val writer = new StringWriter()
      SimpleFormatRenderer().render(writer, tableMap)
      val obtainedResult = writer.toString
      assert(obtainedResult === expectedResult)
    })
  }

  test("rendering Rx") {
    Seq(
      (InputFileName11, ExpectedOutputFileName11),
      (InputFileName12, ExpectedOutputFileName12)
    ).foreach(pair => {
      val tableMap = YamlParser().parse(getFileReader(pair._1)).get
      val expectedResult = readFile(pair._2)
      val writer = new StringWriter()
      RxYamlRenderer().render(writer, tableMap)
      val obtainedResult = writer.toString
      assert(obtainedResult === expectedResult)
    })
  }

  test("rendering JSON Schema") {
    Seq(
      (InputFileName13, ExpectedOutputFileName13)
    ).foreach(pair => {
      val tableMap = YamlParser().parse(getFileReader(pair._1)).get
      val expectedResult = readFile(pair._2)
      val writer = new StringWriter()
      JsonSchemaRenderer().render(writer, tableMap)
      val obtainedResult = writer.toString
      assert(obtainedResult === expectedResult)
    })
  }


}


