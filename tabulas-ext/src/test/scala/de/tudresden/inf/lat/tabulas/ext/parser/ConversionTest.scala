package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{FileReader, StringWriter}
import java.net.URL
import java.nio.file.{Files, Paths}

import de.tudresden.inf.lat.tabulas.ext.renderer.{JsonRenderer, YamlRenderer}
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import org.scalatest.FunSuite

import scala.collection.JavaConverters._

/** This is a test of conversion.
  */
class ConversionTest extends FunSuite {

  final val ExtPrefix = "ext/"

  final val InputFileName0: String = ExtPrefix + "example.properties"
  final val ExpectedOutputFileName0: String = ExtPrefix + "example-expected.properties"

  final val InputFileName1: String = ExtPrefix + "example.properties"
  final val ExpectedOutputFileName1: String = ExtPrefix + "example.json"

  final val InputFileName2: String = ExtPrefix + "multiple_tables.properties"
  final val ExpectedOutputFileName2: String = ExtPrefix + "multiple_tables.json"

  final val InputFileName3: String = ExtPrefix + "miniexample.properties"
  final val ExpectedOutputFileName3: String = ExtPrefix + "miniexample.json"

  final val InputFileName4: String = ExtPrefix + "example.json"
  final val ExpectedOutputFileName4: String = ExtPrefix + "example-expected.properties"

  final val InputFileName5: String = ExtPrefix + "multiple_tables.json"
  final val ExpectedOutputFileName5: String = ExtPrefix + "multiple_tables-expected.properties"

  final val InputFileName6: String = ExtPrefix + "multiple_tables_2.json"
  final val ExpectedOutputFileName6: String = ExtPrefix + "multiple_tables-expected.properties"

  final val InputFileName7: String = ExtPrefix + "example.properties"
  final val ExpectedOutputFileName7: String = ExtPrefix + "example.yaml"

  final val InputFileName8: String = ExtPrefix + "miniexample.properties"
  final val ExpectedOutputFileName8: String = ExtPrefix + "miniexample.yaml"

  final val InputFileName9: String = ExtPrefix + "example.yaml"
  final val ExpectedOutputFileName9: String = ExtPrefix + "example-expected.properties"

  final val InputFileName10: String = ExtPrefix + "multiple_tables.yaml"
  final val ExpectedOutputFileName10: String = ExtPrefix + "multiple_tables-expected.properties"


  final val NewLine: String = "\n"

  def getFileReader(inputFileName: String): FileReader = {
    new FileReader(getPath(inputFileName).getFile)
  }

  def getPath(fileName: String): URL = {
    getClass.getClassLoader.getResource(fileName)
  }

  def readFile(fileName: String): String = {
    val path = Paths.get(getPath(fileName).toURI)
    val result = Files.readAllLines(path).asScala.mkString(NewLine) + NewLine
    result
  }

  test("normalization") {
    val tableMap = SimpleFormatParser().parse(getFileReader(InputFileName0)).get
    val expectedResult = readFile(ExpectedOutputFileName0)
    val writer = new StringWriter()
    val renderer = SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
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
      val renderer = JsonRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
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
      val renderer = SimpleFormatRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
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
      val renderer = YamlRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
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
      val renderer = SimpleFormatRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
    })
  }

}


