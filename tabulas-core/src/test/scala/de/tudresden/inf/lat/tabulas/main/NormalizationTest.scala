package de.tudresden.inf.lat.tabulas.main

import java.io.FileReader
import java.io.StringWriter

import org.junit.Assert
import org.junit.Test

import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * This is a test of normalization of files.
 */
class NormalizationTest {

  val InputFileName0: String = "src/test/resources/example.properties"
  val ExpectedOutputFileName0: String = "src/test/resources/example-expected.properties"

  val InputFileName1: String = "src/test/resources/multiple_tables.properties"
  val ExpectedOutputFileName1: String = "src/test/resources/multiple_tables-expected.properties"

  val NewLine: String = "\n"

  def testNormalizationOfFile(inputFileName: String, expectedFileName: String): Unit = {
    val tableMap: TableMap = new SimpleFormatParser(new FileReader(inputFileName)).parse()
    val expectedResult: String = (new MainTest()).readFile(expectedFileName)
    val writer: StringWriter = new StringWriter()
    val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)
    Assert.assertEquals(expectedResult, writer.toString())
  }

  @Test
  def testNormalization(): Unit = {
    testNormalizationOfFile(InputFileName0, ExpectedOutputFileName0)
    testNormalizationOfFile(InputFileName1, ExpectedOutputFileName1)
  }

}