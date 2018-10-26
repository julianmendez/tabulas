
package de.tudresden.inf.lat.tabulas.datatype

import java.net.{URI, URISyntaxException}
import java.util.Objects

/** This models a URI.
  *
  */
class URIValue(uri: URI) extends PrimitiveTypeValue {

  val SpecialSymbol: String = "#"

  override def getType: PrimitiveType = {
    new URIType()
  }

  def getUri: URI = {
    uri
  }

  def getUriNoLabel: URI = {
    val uriStr = this.uri.toASCIIString
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    val result = if (pos == -1) {
      uri
    } else {
      URIValue.createURI(uriStr.substring(0, pos))
    }
    result
  }

  def getLabel: String = {
    val uriStr = this.uri.toASCIIString
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

  override def hashCode(): Int = {
    uri.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: URIValue => getUri.equals(other.getUri)
      case _ => false
    }
    result
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

  /** Constructs a new URI value using a URI.
    *
    * @param uri URI
    */
  def apply(uri: URI): URIValue = {
    Objects.requireNonNull(uri)
    new URIValue(uri)
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
