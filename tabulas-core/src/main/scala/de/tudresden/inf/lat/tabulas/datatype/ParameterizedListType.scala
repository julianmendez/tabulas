
package de.tudresden.inf.lat.tabulas.datatype

/** This models the type of a list of elements with a parameterized type.
  *
  */
case class ParameterizedListType(parameter: PrimitiveType) extends PrimitiveType {

  final val TypePrefix: String = "List_"

  override def isList: Boolean = {
    true
  }

  def getParameter: PrimitiveType = {
    parameter
  }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = {
    parse(value.render())
  }

  override def parse(str: String): ParameterizedListValue = {
    val list = str.split("\\s+")
      .map(part => parameter.parse(part))
    ParameterizedListValue(parameter, list)
  }

  override def toString: String = {
    getTypeName
  }

  override def getTypeName: String = {
    TypePrefix + parameter.getTypeName
  }

}
