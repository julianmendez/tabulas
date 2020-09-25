package de.tudresden.inf.lat.tabulas.main

import java.io.{FileReader, StringWriter}

import de.tudresden.inf.lat.tabulas.parser.{ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import org.scalatest.funsuite.AnyFunSuite

/** This is a test of normalization of files.
 */
class NormalizationTest extends AnyFunSuite {

  final val InputFileName0: String = "core/example.tab.properties"
  final val ExpectedOutputFileName0: String = "core/example-expected.tab.properties"

  final val InputFileName1: String = "core/multiple_tables.tab.properties"
  final val ExpectedOutputFileName1: String = "core/multiple_tables-expected.tab.properties"

  final val InputFileName2: String = "core/another_example.tab.properties"
  final val ExpectedOutputFileName2: String = "core/another_example-expected.tab.properties"

  final val InputFileName3: String = "core/example.tab.properties"
  final val ExpectedOutputFileName3: String = "core/example-old-expected.tab.properties"

  final val InputFileName4: String = "core/multiple_tables.tab.properties"
  final val ExpectedOutputFileName4: String = "core/multiple_tables-old-expected.tab.properties"

  final val InputFileName5: String = "core/another_example.tab.properties"
  final val ExpectedOutputFileName5: String = "core/another_example-old-expected.tab.properties"

  final val NewLine: String = "\n"

  def testOldFormatParsing(inputFileName: String, expectedFileName: String): Unit = {
    val tableMap = SimpleFormatParser().parse(new FileReader(getPath(inputFileName))).get
    val expectedResult = MainTest().readFile(expectedFileName)
    val writer = new StringWriter()
    val renderer = SimpleFormatRenderer()
    renderer.render(writer, tableMap)
    val obtainedResult = writer.toString
    assert(obtainedResult === expectedResult)
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
        val tableMap = SimpleFormatParser().parse(new FileReader(getPath(inputFileName))).get
        val expectedResult = MainTest().readFile(expectedFileName)
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
        val tableMap = SimpleFormatParser().parse(new FileReader(getPath(inputFileName))).get
        val expectedResult = MainTest().readFile(expectedFileName)
        val writer = new StringWriter()
        val renderer = SimpleFormatRenderer(ParserConstant.Space + ParserConstant.EqualsFieldSign)
        renderer.render(writer, tableMap)
        val obtainedResult = writer.toString
        assert(obtainedResult === expectedResult)
      })
  }

  def getPath(fileName: String): String = {
    getClass.getClassLoader.getResource(fileName).getFile
  }

}

object NormalizationTest {

  def apply(): NormalizationTest = new NormalizationTest

}
