
package de.tudresden.inf.lat.tabulas.table

import java.util.{Comparator, Objects}

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable

/** Comparator for records.
  *
  */
case class RecordComparator() extends Comparator[Record] {

  private val _sortingOrder = new mutable.ArrayBuffer[String]
  private val _fieldsWithReverseOrder = new mutable.TreeSet[String]()

  def this(sortingOrder: Seq[String]) = {
    this()
    this._sortingOrder ++= sortingOrder
  }

  def this(sortingOrder: Seq[String], fieldsWithReverseOrder: Set[String]) = {
    this()
    this._sortingOrder ++= sortingOrder
    this._fieldsWithReverseOrder ++= fieldsWithReverseOrder
  }

  def getSortingOrder: Seq[String] = {
    this._sortingOrder.toSeq
  }

  def getFieldsWithReverseOrder: Set[String] = {
    this._fieldsWithReverseOrder.toSet
  }

  override def compare(record0: Record, record1: Record): Int = {
    val result: Int = if (Objects.isNull(record0)) {
      val res = if (Objects.isNull(record1)) {
        0
      } else {
        -1
      }
      res
    } else {
      val res = if (Objects.isNull(record1)) {
        1
      } else {
        var comparison = 0
        val it: Iterator[String] = this._sortingOrder.iterator
        while (it.hasNext && (comparison == 0)) {
          val token: String = it.next()
          comparison = compareValues(record0.get(token), record1.get(token), this._fieldsWithReverseOrder.contains(token))
        }
        comparison
      }
      res
    }
    result
  }

  def compareValues(optValue0: Option[PrimitiveTypeValue], optValue1: Option[PrimitiveTypeValue], hasReverseOrder: Boolean): Int = {
    val result: Int = if (hasReverseOrder) {
      compareValues(optValue1, optValue0, hasReverseOrder = false)
    } else {
      val res = if (optValue0.isDefined) {
        if (optValue1.isDefined) {
          optValue0.get.compareTo(optValue1.get)
        } else {
          1
        }
      } else {
        if (optValue1.isDefined) {
          -1
        } else {
          0
        }
      }
      res
    }
    result
  }

  override def toString: String = {
    this._sortingOrder.toString
  }

}
