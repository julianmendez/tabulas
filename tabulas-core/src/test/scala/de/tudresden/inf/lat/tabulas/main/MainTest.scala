package de.tudresden.inf.lat.tabulas.main

import java.io.{FileReader, StringWriter}
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table._
import org.scalatest.FunSuite

import scala.collection.JavaConverters._

/** This is a test of modification of a Tabula file.
  */
class MainTest extends FunSuite {

  final val CorePrefix = "core/"

  final val InputFileName: String = CorePrefix + "example.properties"
  final val ExpectedOutputFileName: String = CorePrefix + "example-expected.properties"
  final val ModifiedOutputFileName: String = CorePrefix + "example-modified.properties"

  final val TypeNameRecord: String = "record"
  final val FieldNameAuthors: String = "authors"
  final val FieldNameNumberOfAuthors: String = "numberOfAuthors"
  final val TypeOfNumberOfAuthors: String = "String"
  final val NewLine: String = "\n"


  /**
    * Returns the number of authors for a given record.
    *
    * @param record record
    * @return the number of authors for a given record
    */
  def computeFieldValue(record: Record): StringValue = {
    val value: PrimitiveTypeValue = record.get(FieldNameAuthors).get
    val size: Int = if (Objects.isNull(value)) {
      0
    } else {
      value.renderAsList().size
    }
    val result = new StringValue("" + size)
    result
  }

  def assertContent(tableMap: TableMap, fileName: String): Unit = {
    // Store the table map
    val writer = new StringWriter()
    val renderer = SimpleFormatRenderer(writer)
    renderer.render(tableMap)

    // Read the expected output
    val expectedOutput: String = readFile(fileName)

    // Compare the expected output with the actual output
    assert(expectedOutput === writer.toString)
  }

  def readFile(fileName: String): String = {
    val path = Paths.get(getPath(fileName).toURI)
    val result = Files.readAllLines(path).asScala.mkString(NewLine) + NewLine
    result
  }

  def getPath(fileName: String): URL = {
    getClass.getClassLoader.getResource(fileName)
  }

  test("testAddNewField") {

    // This is an example of source code where the number of authors is
    // a computed value

    // Read the table map
    val oldTableMap = new SimpleFormatParser(new FileReader(getPath(InputFileName).getFile)).parse()

    // Make a copy of the tableMap
    val tableMap: TableMapImpl = TableMapImpl(oldTableMap)

    assertContent(tableMap, ExpectedOutputFileName)

    // Get the main table
    val table: Table = tableMap.getTable(TypeNameRecord).get

    // Make a copy of the main table
    var newTable: TableImpl = TableImpl(table)

    // Get type of main table
    val oldType: CompositeType = table.getType

    // Make a copy of type
    // val newType: CompositeTypeImpl = new CompositeTypeImpl(oldType)
    var newType = oldType.getFields
      .foldLeft(CompositeTypeImpl())((compType, field) => compType.declareField(field, oldType.getFieldType(field).get).get)

    // Add new declaration with number of authors
    if (!newType.getFields.contains(FieldNameNumberOfAuthors)) {
      newType = newType.declareField(FieldNameNumberOfAuthors, TypeOfNumberOfAuthors).get
    }

    // Update type of table
    newTable = TableImpl(newType, newTable)

    // Update the map of URI prefixes
    newTable = newTable.copy(prefixMap = table.getPrefixMap)

    // Compute the number of authors for each record
    val newRecords = newTable.getRecords
      .map(record => RecordImpl(record).set(FieldNameNumberOfAuthors, computeFieldValue(record)))

    newTable = newTable.copy(records = newRecords)

    // Add the new table to the new table map
    val newTableMap = tableMap.put(TypeNameRecord, newTable)

    assertContent(newTableMap, ModifiedOutputFileName)
  }

}

object MainTest {

  def apply(): MainTest = new MainTest

}
