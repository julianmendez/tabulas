
package de.tudresden.inf.lat.tabulas.datatype

import java.net.{URI, URISyntaxException}
import java.util.Objects

/** This models a URI.
  *
  */
case class URIValue(uri: URI) extends PrimitiveTypeValue {

  final val SpecialSymbol: String = "#"

  override def getType: PrimitiveType = {
    URIType()
  }

  def getUriNoLabel: URI = {
    val uriStr = uri.toString
    val pos = uriStr.lastIndexOf(SpecialSymbol)
    val result = if (pos == -1) {
      uri
    } else {
      URIValue.createURI(uriStr.substring(0, pos))
    }
    result
  }

  def getLabel: String = {
    val uriStr = uri.toString
    val pos = uriStr.lastIndexOf(SpecialSymbol)
    val result = if (pos == -1) {
      ""
    } else {
      uriStr.substring(pos + SpecialSymbol.length())
    }
    result
  }

  override def isEmpty: Boolean = {
    getUri.toString.trim().isEmpty
  }

  def getUri: URI = {
    uri
  }

  override def renderAsList(): Seq[String] = {
    List(render())
  }

  override def render(): String = {
    uri.toString
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

  override def toString: String = {
    render()
  }

}

object URIValue {

  def apply(): URIValue = {
    URIValue(createURI(""))
  }

  /** Constructs a new URI value using a string.
    *
    * @param uriStr URI
    */
  def apply(uriStr: String): URIValue = {
    Objects.requireNonNull(uriStr)
    URIValue(createURI(uriStr))
  }

  def createURI(uriStr: String): URI = {
    val result = try {
      new URI(uriStr)
    } catch {
      case e: URISyntaxException => throw new ParseException("Invalid URI '" + uriStr + "'.", e)
    }
    result
  }

  /** Constructs a new URI value using another URI value.
    *
    * @param other URI value
    */
  def apply(other: URIValue): URIValue = {
    new URIValue(other.getUri)
  }

}
