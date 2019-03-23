
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

  def getUri: URI = {
    uri
  }

  def getUriNoLabel: URI = {
    val uriStr = uri.toASCIIString
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    val result = if (pos == -1) {
      uri
    } else {
      URIValue.createURI(uriStr.substring(0, pos))
    }
    result
  }

  def getLabel: String = {
    val uriStr = uri.toASCIIString
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    val result: String = if (pos == -1) {
      ""
    } else {
      uriStr.substring(pos + SpecialSymbol.length())
    }
    result
  }

  override def isEmpty: Boolean = {
    getUri.toASCIIString.trim().isEmpty
  }

  override def render(): String = {
    uri.toASCIIString
  }

  override def renderAsList(): Seq[String] = {
    List(render())
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
    new URIValue(createURI(""))
  }

  /** Constructs a new URI value using a string.
    *
    * @param uriStr URI
    */
  def apply(uriStr: String): URIValue = {
    Objects.requireNonNull(uriStr)
    new URIValue(createURI(uriStr))
  }

  /** Constructs a new URI value using another URI value.
    *
    * @param other URI value
    */
  def apply(other: URIValue): URIValue = {
    new URIValue(other.getUri)
  }

  def createURI(uriStr: String): URI = {
    val result = try {
      new URI(uriStr)
    } catch {
      case e: URISyntaxException => throw new ParseException("Invalid URI '" + uriStr + "'.", e)
    }
    result
  }

}
