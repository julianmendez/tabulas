package de.tudresden.inf.lat.tabulas.main

import java.io.{FileReader, StringWriter}

import org.scalatest.FunSuite

import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/** This is a test of normalization of files.
  */
class NormalizationTest extends FunSuite {

  val InputFileName0: String = "example.properties"
  val ExpectedOutputFileName0: String = "example-expected.properties"

  val InputFileName1: String = "multiple_tables.properties"
  val ExpectedOutputFileName1: String = "multiple_tables-expected.properties"

  val InputFileName2: String = "another_example.properties"
  val ExpectedOutputFileName2: String = "another_example-expected.properties"

  val NewLine: String = "\n"

  def getPath(fileName: String): String = { getClass.getClassLoader.getResource(fileName).getFile }

  def testNormalizationOfFile(inputFileName: String, expectedFileName: String): Unit = {
    val tableMap: TableMap = new SimpleFormatParser(new FileReader(getPath(inputFileName))).parse()
    val expectedResult: String = (new MainTest()).readFile(expectedFileName)
    val writer: StringWriter = new StringWriter()
    val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    assert(expectedResult === writer.toString)
  }

  test("testNormalization") {
    testNormalizationOfFile(InputFileName0, ExpectedOutputFileName0)
    testNormalizationOfFile(InputFileName1, ExpectedOutputFileName1)
    testNormalizationOfFile(InputFileName2, ExpectedOutputFileName2)
  }

}

object NormalizationTest {

  def apply(): NormalizationTest = new NormalizationTest

}
