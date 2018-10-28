package de.tudresden.inf.lat.tabulas.main

import java.io.{BufferedReader, FileReader, StringWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.{Table, TableImpl, TableMap, TableMapImpl}
import org.scalatest.FunSuite

/** This is a test of modification of a Tabula file.
  */
class MainTest extends FunSuite {

  val InputFileName: String = "example.properties"
  val ExpectedOutputFileName: String = "example-expected.properties"
  val ModifiedOutputFileName: String = "example-modified.properties"

  val TypeNameRecord: String = "record"
  val FieldNameAuthors: String = "authors"
  val FieldNameNumberOfAuthors: String = "numberOfAuthors"
  val TypeOfNumberOfAuthors: String = "String"
  val NewLine: String = "\n"

  def getPath(fileName: String): String = { getClass.getClassLoader.getResource(fileName).getFile }

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

  def readFile(fileName: String): String = {
    val sb = new StringBuilder()
    val reader = new BufferedReader(new FileReader(getPath(fileName)))
    reader.lines().toArray().foreach(obj => {
      val line = obj.asInstanceOf[String]
      sb.append(line + NewLine)
    })
    reader.close()
    val result = sb.toString
    result
  }

  def assertContent(tableMap: TableMap, fileName: String): Unit = {
    // Store the table map
    val writer = new StringWriter()
    val renderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)

    // Read the expected output
    val expectedOutput: String = readFile(fileName)

    // Compare the expected output with the actual output
    assert(expectedOutput === writer.toString)
  }

  test("testAddNewField") {

    // This is an example of source code where the number of authors is
    // a computed value

    // Read the table map
    val oldTableMap: TableMap = new SimpleFormatParser(new FileReader(getPath(InputFileName))).parse()

    // Make a copy of the tableMap
    // val newTableMap: TableMapImpl = new TableMapImpl(oldTableMap)
    val newTableMap: TableMapImpl = new TableMapImpl()
    oldTableMap.getTableIds.foreach(tableId => newTableMap.put(tableId, oldTableMap.getTable(tableId).get))

    assertContent(newTableMap, ExpectedOutputFileName)

    // Get the main table
    val table: Table = newTableMap.getTable(TypeNameRecord).get

    // Make a copy of the main table
    val newTable: TableImpl = new TableImpl(table)

    // Add the new table to the new table map
    newTableMap.put(TypeNameRecord, newTable)

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
    newTable.setType(newType)

    // Update the map of URI prefixes
    newTable.setPrefixMap(table.getPrefixMap)

    // Compute the number of authors for each record
    table.getRecords.foreach(record => record.set(FieldNameNumberOfAuthors, computeFieldValue(record)))

    assertContent(newTableMap, ModifiedOutputFileName)
  }

}

object MainTest {

  def apply(): MainTest = new MainTest

}
