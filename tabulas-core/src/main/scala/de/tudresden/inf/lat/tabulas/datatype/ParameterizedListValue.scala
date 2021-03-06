
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/** This models a list of elements with a parameterized type.
 *
 */
case class ParameterizedListValue(parameter: PrimitiveType, list: Seq[PrimitiveTypeValue])
  extends PrimitiveTypeValue {

  final val Separator: String = " "

  override val getType: PrimitiveType = ParameterizedListType(parameter)

  override val renderAsList: Seq[String] = list.map(_.render)

  override val render: String = renderAsList.mkString(Separator)

  override val isEmpty: Boolean = list.isEmpty

  val getList: Seq[PrimitiveTypeValue] = list

  val getParameter: PrimitiveType = parameter

  override def compareTo(obj: PrimitiveTypeValue): Int = {
    val result = obj match {
      case other: ParameterizedListValue =>
        val diff = getList.length - other.getList.length
        if (diff == 0) {
          toString.compareTo(other.toString)
        } else {
          diff
        }
      case _ =>
        toString.compareTo(obj.toString)
    }
    result
  }

}

object ParameterizedListValue {

  /** Constructs a new parameterized list value.
   *
   * @param parameter primitive type
   */
  def apply(parameter: PrimitiveType): ParameterizedListValue = {
    Objects.requireNonNull(parameter)
    ParameterizedListValue(parameter, Seq())
  }

  /** Constructs a new parameterized list value using another parameterized
   * list value.
   *
   * @param other parameterized list value
   */
  def apply(other: ParameterizedListValue): ParameterizedListValue = {
    Objects.requireNonNull(other)
    ParameterizedListValue(other.getParameter, other.getList)
  }

}
