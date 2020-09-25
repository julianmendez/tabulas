
package de.tudresden.inf.lat.tabulas.table

import java.util.{Comparator, Objects}

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

/** Comparator for records.
 *
 */
case class RecordComparator(sortingOrder: Seq[String], fieldsWithReverseOrder: Set[String]) extends Comparator[Record] {

  override val toString: String = sortingOrder.toString

  val getSortingOrder: Seq[String] = sortingOrder

  val getFieldsWithReverseOrder: Set[String] = fieldsWithReverseOrder

  override def compare(record0: Record, record1: Record): Int = {
    val result = if (Objects.isNull(record0)) {
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
        val maybeDifference = sortingOrder.find(token => {
          val comparison = compareValues(record0.get(token), record1.get(token), fieldsWithReverseOrder.contains(token))
          comparison != 0
        })
          .map(token =>
            compareValues(record0.get(token), record1.get(token), fieldsWithReverseOrder.contains(token)))
        maybeDifference.getOrElse(0)
      }
      res
    }
    result
  }

  def compareValues(optValue0: Option[PrimitiveTypeValue], optValue1: Option[PrimitiveTypeValue], hasReverseOrder: Boolean): Int = {
    val result = if (hasReverseOrder) {
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

}

object RecordComparator {}

