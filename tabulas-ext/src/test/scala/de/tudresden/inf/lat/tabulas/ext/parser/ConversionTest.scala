package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{FileReader, StringWriter}

import de.tudresden.inf.lat.tabulas.ext.renderer.{JsonRenderer, YamlRenderer}
import de.tudresden.inf.lat.tabulas.main.MainTest
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap
import org.scalatest.FunSuite

/** This is a test of conversion.
  */
class ConversionTest extends FunSuite {

  val InputFileName0: String = "example.properties"
  val ExpectedOutputFileName0: String = "example-expected.properties"

  val InputFileName1: String = "example.properties"
  val ExpectedOutputFileName1: String = "example.json"

  val InputFileName2: String = "example.json"
  val ExpectedOutputFileName2: String = "example-expected.properties"

  val InputFileName3: String = "example.properties"
  val ExpectedOutputFileName3: String = "example.yml"

  val NewLine: String = "\n"

  def getPath(fileName: String): String = {
    getClass.getClassLoader.getResource(fileName).getFile
  }

  def getFileReader(inputFileName: String): FileReader = {
    new FileReader(getPath(inputFileName))
  }

  test("normalization") {
    val tableMap: TableMap = new SimpleFormatParser(getFileReader(InputFileName0)).parse()
    val expectedResult: String = (new MainTest()).readFile(ExpectedOutputFileName0)
    val writer = new StringWriter()
    val renderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

  test("rendering JSON") {
    val tableMap: TableMap = new SimpleFormatParser(getFileReader(InputFileName1)).parse()
    val expectedResult: String = (new MainTest()).readFile(ExpectedOutputFileName1)
    val writer = new StringWriter()
    val renderer = new JsonRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

  test("parsing JSON") {
    val tableMap: TableMap = new JsonParser(getFileReader(InputFileName2)).parse()
    val expectedResult: String = (new MainTest()).readFile(ExpectedOutputFileName2)
    val writer = new StringWriter()
    val renderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

  test("rendering YAML") {
    val tableMap: TableMap = new SimpleFormatParser(getFileReader(InputFileName3)).parse()
    val expectedResult: String = (new MainTest()).readFile(ExpectedOutputFileName3)
    val writer = new StringWriter()
    val renderer = new YamlRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

}


