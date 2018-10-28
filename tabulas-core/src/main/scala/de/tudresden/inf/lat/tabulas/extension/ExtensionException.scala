package de.tudresden.inf.lat.tabulas.extension

/** Extension exception.
  */
case class ExtensionException(message: String, cause: Throwable) extends RuntimeException(message, cause)

object ExtensionException {

  def apply(message: String = ""): ExtensionException = ExtensionException(message, None.orNull)

}

