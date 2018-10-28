
package de.tudresden.inf.lat.tabulas.datatype

/** This models the type of a list of elements with a parameterized type.
  *
  */
class ParameterizedListType(parameter: PrimitiveType) extends PrimitiveType {

  val TypePrefix: String = "List_"


  override def getTypeName: String = {
    TypePrefix + parameter.getTypeName
  }

  override def isList: Boolean = {
    true
  }

  override def parse(str: String): ParameterizedListValue = {
    val elements = str.split("\\s+")
      .map(part => parameter.parse(part))
    val result = new ParameterizedListValue(parameter)
    elements.foreach(element => result += element)
    result
  }

  def getParameter: PrimitiveType = {
    parameter
  }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = {
    parse(value.render())
  }

  override def hashCode(): Int = {
    parameter.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: ParameterizedListType => getParameter.equals(other.getParameter)
      case _ => false
    }
    result
  }

  override def toString: String = {
    getTypeName
  }

}
