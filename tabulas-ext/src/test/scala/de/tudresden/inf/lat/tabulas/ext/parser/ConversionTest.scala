package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{FileReader, StringWriter}
import java.net.URL
import java.nio.file.{Files, Paths}

import de.tudresden.inf.lat.tabulas.ext.renderer.{JsonRenderer, YamlRenderer}
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap
import org.scalatest.FunSuite

import scala.collection.JavaConverters._

/** This is a test of conversion.
  */
class ConversionTest extends FunSuite {

  val InputFileName0: String = "example.properties"
  val ExpectedOutputFileName0: String = "example-expected.properties"

  val InputFileName1: String = "example.properties"
  val ExpectedOutputFileName1: String = "example.json"

  val InputFileName2: String = "multiple_tables.properties"
  val ExpectedOutputFileName2: String = "multiple_tables.json"

  val InputFileName3: String = "example.json"
  val ExpectedOutputFileName3: String = "example-expected.properties"

  val InputFileName4: String = "multiple_tables.json"
  val ExpectedOutputFileName4: String = "multiple_tables-expected.properties"

  val InputFileName5: String = "multiple_tables_2.json"
  val ExpectedOutputFileName5: String = "multiple_tables-expected.properties"

  val InputFileName6: String = "example.properties"
  val ExpectedOutputFileName6: String = "example.yml"

  val InputFileName7: String = "miniexample.properties"
  val ExpectedOutputFileName7: String = "miniexample.yml"

  val NewLine: String = "\n"

  def getPath(fileName: String): URL = {
    getClass.getClassLoader.getResource(fileName)
  }

  def getFileReader(inputFileName: String): FileReader = {
    new FileReader(getPath(inputFileName).getFile)
  }

  def readFile(fileName: String): String = {
    val path = Paths.get(getPath(fileName).toURI)
    val result = Files.readAllLines(path).asScala.mkString(NewLine) + NewLine
    result
  }

  test("normalization") {
    val tableMap: TableMap = new SimpleFormatParser(getFileReader(InputFileName0)).parse()
    val expectedResult: String = readFile(ExpectedOutputFileName0)
    val writer = new StringWriter()
    val renderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

  test("rendering JSON") {
    Seq(
      (InputFileName1, ExpectedOutputFileName1),
      (InputFileName2, ExpectedOutputFileName2)
    ).foreach(pair => {
      val tableMap: TableMap = new SimpleFormatParser(getFileReader(pair._1)).parse()
      val expectedResult: String = readFile(pair._2)
      val writer = new StringWriter()
      val renderer = new JsonRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
    })
  }

  test("parsing JSON") {
    Seq(
      (InputFileName3, ExpectedOutputFileName3),
      (InputFileName4, ExpectedOutputFileName4),
      (InputFileName5, ExpectedOutputFileName5)
    ).foreach(pair => {
      val tableMap: TableMap = new JsonParser(getFileReader(pair._1)).parse()
      val expectedResult: String = readFile(pair._2)
      val writer = new StringWriter()
      val renderer = new SimpleFormatRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
    })
  }

  test("rendering YAML") {
    Seq(
      (InputFileName6, ExpectedOutputFileName6),
      (InputFileName7, ExpectedOutputFileName7)
    ).foreach(pair => {
      val tableMap: TableMap = new SimpleFormatParser(getFileReader(pair._1)).parse()
      val expectedResult: String = readFile(pair._2)
      val writer = new StringWriter()
      val renderer = new YamlRenderer(writer)
      renderer.render(tableMap)
      assert(expectedResult === writer.toString)
    })
  }

}


