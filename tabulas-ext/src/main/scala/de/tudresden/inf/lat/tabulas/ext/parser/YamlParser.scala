
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io._

import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMapImpl
import org.snakeyaml.engine.v1.api.{Load, LoadSettingsBuilder}

import scala.collection.JavaConverters._
import scala.util.Try

/** Parser for YAML format.
  */
case class YamlParser(permissive: Boolean) extends Parser {

  override def parse(input: Reader): Try[TableMapImpl] = Try {
    val buffer = transformDocument(input)
    val newReader = new BufferedReader(
      new InputStreamReader(new ByteArrayInputStream(buffer.getBytes())))

    SimpleFormatParser(permissive).parse(newReader).get
  }

  def transformDocument(reader: Reader): String = {
    val settings = new LoadSettingsBuilder().build
    val load = new Load(settings)
    val yaml = load.loadAllFromReader(reader)
    val parts = yaml.iterator().asScala.toSeq
    val result = SimpleFormatRenderer.Header +
      parts.map(table => transformTable(table)).mkString
    result
  }

  def transformTable(table: Any): String = {
    val list = table.asInstanceOf[java.util.List[Any]].asScala
    val result = ParserConstant.NewLine + ParserConstant.NewLine +
      list.map(record => transformRecord(record)).mkString +
      ParserConstant.NewLine
    result
  }

  def transformRecord(record: Any): String = {
    val mapOfEntries = record.asInstanceOf[java.util.LinkedHashMap[String, Any]].asScala
    val prefix = if (mapOfEntries.contains(ParserConstant.TypeSelectionToken)) {
      ""

    } else {
      ParserConstant.NewLine + ParserConstant.NewRecordToken + ParserConstant.Space +
        ParserConstant.ColonFieldSign + ParserConstant.NewLine

    }
    val middle = mapOfEntries.map(pair => {
      val keyAny = pair._1
      val valueAny = pair._2
      val key = keyAny.asInstanceOf[String]
      renderEntry(key, valueAny)
    }).mkString
    val result = prefix + middle + ParserConstant.NewLine
    result
  }


  def renderEntry(key: String, value: Any): String = {
    val prefix = key + ParserConstant.Space + ParserConstant.ColonFieldSign

    val result = if (Option(value).isEmpty) {
      ""

    } else if (key == ParserConstant.TypeSelectionToken) {
      prefix + renderMetadata(value) + ParserConstant.NewLine

    } else {
      val prefixForValue = value match {
        case _: java.util.List[Any] => ""
        case _ => ParserConstant.Space
      }
      ParserConstant.Space + prefix + prefixForValue + asString(value) + ParserConstant.NewLine

    }
    result
  }

  def renderMetadata(metadataAny: Any): String = {
    val metadata = metadataAny.asInstanceOf[java.util.Map[String, Any]].asScala
    val result = ParserConstant.NewLine + metadata.map(pair => {
      val key = pair._1
      val value = pair._2
      val line = ParserConstant.Space + key + ParserConstant.Space + ParserConstant.ColonFieldSign +
        ParserConstant.Space + asString(value)
      line
    }).mkString(ParserConstant.NewLine)
    result
  }

  def asString(value: Any): String = {
    val itemSeparator = ParserConstant.Space + ParserConstant.LineContinuationSymbol +
      ParserConstant.NewLine + ParserConstant.Space + ParserConstant.Space
    if (Option(value).isEmpty) {
      ""

    } else {
      value match {
        case valueStr: String => valueStr
        case valueList: java.util.List[String] =>
          itemSeparator + valueList.asScala.mkString(itemSeparator)
        case _ => value.toString
      }
    }
  }

}

object YamlParser {

  def apply(): YamlParser = YamlParser(false)

}
