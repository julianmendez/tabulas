package de.tudresden.inf.lat.tabulas.main

import java.io.BufferedReader
import java.io.FileReader
import java.io.StringWriter
import java.util.Objects

import scala.collection.JavaConversions.asScalaBuffer

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

  val InputFileName: String = "src/test/resources/example.properties"
  val ExpectedOutputFileName: String = "src/test/resources/example-modified.properties"

  val TypeNameRecord: String = "record"
  val FieldNameAuthors: String = "authors"
  val FieldNameNumberOfAuthors: String = "numberOfAuthors"
  val TypeOfNumberOfAuthors: String = "String"
  val NewLine: String = "\n"

  /**
   * Returns the number of authors for a given record.
   *
   * @param record
   *            record
   * @return the number of authors for a given record
   */
  def computeFieldValue(record: Record): StringValue = {
    val value: PrimitiveTypeValue = record.get(FieldNameAuthors)
    val size: Int = if (Objects.isNull(value)) { 0 } else { value.renderAsList().size() }
    new StringValue("" + size)
  }

  @Test
  def addNewFieldOldTest(): Unit = {

    // This is an example of source code where the number of authors is
    // a computed value

    // Read the table map
    val oldTableMap: TableMap = new SimpleFormatParser(new FileReader(InputFileName)).parse()

    // Make a copy of the tableMap
    // val newTableMap: TableMapImpl = new TableMapImpl(oldTableMap)
    val newTableMap: TableMapImpl = new TableMapImpl()
    oldTableMap.getTableIds().foreach(tableId => newTableMap.put(tableId, oldTableMap.getTable(tableId)))

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
    oldType.getFields().foreach(field => newType.declareField(field, oldType.getFieldType(field).get()))

    // Add new declaration with number of authors
    if (!newType.getFields().contains(FieldNameNumberOfAuthors)) {
      newType.declareField(FieldNameNumberOfAuthors, TypeOfNumberOfAuthors)
    }

    // Update type of table
    newTable.setType(newType)

    // Compute the number of authors for each record
    table.getRecords().foreach(record => record.set(FieldNameNumberOfAuthors, computeFieldValue(record)))

    // Store the new table map
    val writer: StringWriter = new StringWriter()
    val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(writer)
    renderer.render(newTableMap)

    // Read the expected output
    val sbuf: StringBuffer = new StringBuffer()
    val reader: BufferedReader = new BufferedReader(new FileReader(ExpectedOutputFileName))
    var line = reader.readLine()
    while (Objects.nonNull(line)) {
      sbuf.append(line + NewLine)
      line = reader.readLine()
    }
    reader.close()

    // Compare the expected output with the actual output
    Assert.assertEquals(sbuf.toString(), writer.toString())
  }

}
