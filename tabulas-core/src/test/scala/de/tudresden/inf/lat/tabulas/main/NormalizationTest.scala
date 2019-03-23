package de.tudresden.inf.lat.tabulas.main

import java.io.{FileReader, StringWriter}

import de.tudresden.inf.lat.tabulas.parser.{ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap
import org.scalatest.FunSuite

/** This is a test of normalization of files.
  */
class NormalizationTest extends FunSuite {

  final val InputFileName0: String = "core/example.properties"
  final val ExpectedOutputFileName0: String = "core/example-expected.properties"

  final val InputFileName1: String = "core/multiple_tables.properties"
  final val ExpectedOutputFileName1: String = "core/multiple_tables-expected.properties"

  final val InputFileName2: String = "core/another_example.properties"
  final val ExpectedOutputFileName2: String = "core/another_example-expected.properties"

  final val InputFileName3: String = "core/example.properties"
  final val ExpectedOutputFileName3: String = "core/example-old-expected.properties"

  final val InputFileName4: String = "core/multiple_tables.properties"
  final val ExpectedOutputFileName4: String = "core/multiple_tables-old-expected.properties"

  final val InputFileName5: String = "core/another_example.properties"
  final val ExpectedOutputFileName5: String = "core/another_example-old-expected.properties"

  final val NewLine: String = "\n"

  def getPath(fileName: String): String = {
    getClass.getClassLoader.getResource(fileName).getFile
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
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(getPath(inputFileName))).parse()
        val expectedResult: String = (new MainTest()).readFile(expectedFileName)
        val writer = new StringWriter()
        val renderer = SimpleFormatRenderer(writer)
        renderer.render(tableMap)
        assert(expectedResult === writer.toString)
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
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(getPath(inputFileName))).parse()
        val expectedResult: String = (new MainTest()).readFile(expectedFileName)
        val writer = new StringWriter()
        val renderer = SimpleFormatRenderer(writer, ParserConstant.EqualsFieldSign)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
      })
  }


  def testOldFormatParsing(inputFileName: String, expectedFileName: String): Unit = {
    val tableMap: TableMap = new SimpleFormatParser(new FileReader(getPath(inputFileName))).parse()
    val expectedResult: String = (new MainTest()).readFile(expectedFileName)
    val writer = new StringWriter()
    val renderer = SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

}

object NormalizationTest {

  def apply(): NormalizationTest = new NormalizationTest

}
