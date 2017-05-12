package de.tudresden.inf.lat.tabulas.main

import java.io.BufferedReader
import java.io.FileReader
import java.io.StringWriter
import java.util.Objects

import scala.collection.JavaConverters.asScalaBufferConverter

import org.junit.Assert
import org.junit.Test

import de.tudresden.inf.lat.tabulas.datatype.CompositeType
import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeImpl
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableImpl
import de.tudresden.inf.lat.tabulas.table.TableMap
import de.tudresden.inf.lat.tabulas.table.TableMapImpl

/**
 * This is a test of modification of a Tabula file.
 */
class MainTest {

  val InputFileName: String = "example.properties"
  val ExpectedOutputFileName: String = "example-expected.properties"
  val ModifiedOutputFileName: String = "example-modified.properties"

  val TypeNameRecord: String = "record"
  val FieldNameAuthors: String = "authors"
  val FieldNameNumberOfAuthors: String = "numberOfAuthors"
  val TypeOfNumberOfAuthors: String = "String"
  val NewLine: String = "\n"

  def getPath(fileName: String): String = {
    return getClass().getClassLoader().getResource(fileName).getFile()
  }

  /**
   * Returns the number of authors for a given record.
   *
   * @param record
   *            record
   * @return the number of authors for a given record
   */
  def computeFieldValue(record: Record): StringValue = {
    val value: PrimitiveTypeValue = record.get(FieldNameAuthors).get
    val size: Int = if (Objects.isNull(value)) { 0 } else { value.renderAsList().size }
    return new StringValue("" + size)
  }

  def readFile(fileName: String): String = {
    val sb: StringBuilder = new StringBuilder()
    val reader: BufferedReader = new BufferedReader(new FileReader(getPath(fileName)))
    reader.lines().toArray().foreach(obj => {
      val line = obj.asInstanceOf[String]
      sb.append(line + NewLine)
    })
    reader.close()
    return sb.toString()
  }

  def assertContent(tableMap: TableMap, fileName: String): Unit = {
    // Store the table map
    val writer: StringWriter = new StringWriter()
    val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(writer)
    renderer.render(tableMap)

    // Read the expected output
    val expectedOutput: String = readFile(fileName)

    // Compare the expected output with the actual output
    Assert.assertEquals(expectedOutput, writer.toString())
  }

  @Test
  def testAddNewField(): Unit = {

    // This is an example of source code where the number of authors is
    // a computed value

    // Read the table map
    val oldTableMap: TableMap = new SimpleFormatParser(new FileReader(getPath(InputFileName))).parse()

    // Make a copy of the tableMap
    // val newTableMap: TableMapImpl = new TableMapImpl(oldTableMap)
    val newTableMap: TableMapImpl = new TableMapImpl()
    oldTableMap.getTableIds().foreach(tableId => newTableMap.put(tableId, oldTableMap.getTable(tableId)))

    assertContent(newTableMap, ExpectedOutputFileName)

    // Get the main table
    val table: Table = newTableMap.getTable(TypeNameRecord)

    // Make a copy of the main table
    val newTable: TableImpl = new TableImpl(table)

    // Add the new table to the new table map
    newTableMap.put(TypeNameRecord, newTable)

    // Get type of main table
    val oldType: CompositeType = table.getType()

    // Make a copy of type
    // val newType: CompositeTypeImpl = new CompositeTypeImpl(oldType)
    val newType: CompositeTypeImpl = new CompositeTypeImpl()
    oldType.getFields().foreach(field => newType.declareField(field, oldType.getFieldType(field).get))

    // Add new declaration with number of authors
    if (!newType.getFields().contains(FieldNameNumberOfAuthors)) {
      newType.declareField(FieldNameNumberOfAuthors, TypeOfNumberOfAuthors)
    }

    // Update type of table
    newTable.setType(newType)

    // Compute the number of authors for each record
    table.getRecords().foreach(record => record.set(FieldNameNumberOfAuthors, computeFieldValue(record)))

    assertContent(newTableMap, ModifiedOutputFileName)
  }

}
