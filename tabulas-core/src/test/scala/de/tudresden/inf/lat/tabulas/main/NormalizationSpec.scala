package de.tudresden.inf.lat.tabulas.main

import java.io.{FileReader, StringWriter}
import java.net.URL

import de.tudresden.inf.lat.tabulas.parser.{ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import org.scalatest.funsuite.AnyFunSuite

import scala.language.implicitConversions

/** This is a test of normalization of files.
 */
case class NormalizationSpec() extends AnyFunSuite {

  final val CorePrefix: String = "core/"

  final val InputFileName0: String = CorePrefix + "example.tab.properties"
  final val ExpectedOutputFileName0: String = CorePrefix + "example-expected.tab.properties"

  final val InputFileName1: String = CorePrefix + "multiple_tables.tab.properties"
  final val ExpectedOutputFileName1: String = CorePrefix + "multiple_tables-expected.tab.properties"

  final val InputFileName2: String = CorePrefix + "another_example.tab.properties"
  final val ExpectedOutputFileName2: String = CorePrefix + "another_example-expected.tab.properties"

  final val InputFileName3: String = CorePrefix + "example.tab.properties"
  final val ExpectedOutputFileName3: String = CorePrefix + "example-old-expected.tab.properties"

  final val InputFileName4: String = CorePrefix + "multiple_tables.tab.properties"
  final val ExpectedOutputFileName4: String = CorePrefix + "multiple_tables-old-expected.tab.properties"

  final val InputFileName5: String = CorePrefix + "another_example.tab.properties"
  final val ExpectedOutputFileName5: String = CorePrefix + "another_example-old-expected.tab.properties"

  def testOldFormatParsing(inputFileName: String, expectedFileName: String): Unit = {
    val tableMap = SimpleFormatParser().parse(getFileReader(inputFileName)).get
    val expectedResult = MainSpec().readFile(expectedFileName)
    val writer = new StringWriter()
    val renderer = SimpleFormatRenderer()
    renderer.render(writer, tableMap)
    val obtainedResult = writer.toString
    assert(obtainedResult === expectedResult)
  }

  def getFileReader(inputFileName: String): FileReader = {
    new FileReader(getPath(inputFileName).getFile)
  }

  def getPath(fileName: String): URL = {
    getClass.getClassLoader.getResource(fileName)
  }

  test("test normalization") {
    Seq((InputFileName0, ExpectedOutputFileName0),
      (InputFileName1, ExpectedOutputFileName1),
      (InputFileName2, ExpectedOutputFileName2),
      (ExpectedOutputFileName3, ExpectedOutputFileName0),
      (ExpectedOutputFileName4, ExpectedOutputFileName1),
      (ExpectedOutputFileName5, ExpectedOutputFileName2))
      .foreach(pair => {

        val inputFileName = pair._1
        val expectedFileName = pair._2
        val tableMap = SimpleFormatParser().parse(getFileReader(inputFileName)).get
        val expectedResult = MainSpec().readFile(expectedFileName)
        val writer = new StringWriter()
        val renderer = SimpleFormatRenderer()
        renderer.render(writer, tableMap)
        val obtainedResult = writer.toString
        assert(obtainedResult === expectedResult)
      }
      )
  }


  test("test old format normalization") {
    Seq((InputFileName3, ExpectedOutputFileName3),
      (InputFileName4, ExpectedOutputFileName4),
      (InputFileName5, ExpectedOutputFileName5))
      .foreach(pair => {
        val inputFileName = pair._1
        val expectedFileName = pair._2
        val tableMap = SimpleFormatParser().parse(getFileReader(inputFileName)).get
        val expectedResult = MainSpec().readFile(expectedFileName)
        val writer = new StringWriter()
        val renderer = SimpleFormatRenderer(ParserConstant.Space + ParserConstant.EqualsFieldSign)
        renderer.render(writer, tableMap)
        val obtainedResult = writer.toString
        assert(obtainedResult === expectedResult)
      })
  }

}

object NormalizationSpec {}

