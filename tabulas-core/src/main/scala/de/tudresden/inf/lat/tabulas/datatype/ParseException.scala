package de.tudresden.inf.lat.tabulas.datatype

/** Parse exception.
  */
case class ParseException(message: String, cause: Throwable) extends RuntimeException(message, cause)

object ParseException {

  def apply(message: String = ""): ParseException = ParseException(message, None.orNull)

}

