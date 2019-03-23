
package de.tudresden.inf.lat.tabulas.datatype

/** This models the type of a list of elements with a parameterized type.
  *
  */
case class ParameterizedListType(parameter: PrimitiveType) extends PrimitiveType {

  final val TypePrefix: String = "List_"

  override def getTypeName: String = {
    TypePrefix + parameter.getTypeName
  }

  override def isList: Boolean = {
    true
  }

  override def parse(str: String): ParameterizedListValue = {
    val list = str.split("\\s+")
      .map(part => parameter.parse(part))
    ParameterizedListValue(parameter, list)
  }

  def getParameter: PrimitiveType = {
    parameter
  }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = {
    parse(value.render())
  }

  override def toString: String = {
    getTypeName
  }

}
