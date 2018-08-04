
package de.tudresden.inf.lat.tabulas.datatype

import java.net.{URI, URISyntaxException}
import java.util.Objects

import scala.collection.mutable

/** This models a URI.
  *
  */
class URIValue(uri: URI) extends PrimitiveTypeValue {

  val SpecialSymbol: String = "#"

  override def getType: PrimitiveType = {
    new URIType()
  }

  def getUri: URI = {
    this.uri
  }

  def getUriNoLabel: URI = {
    val uriStr: String = this.uri.toASCIIString
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    var result: URI = this.uri
    if (pos == -1) {
      result = this.uri
    } else {
      result = URIValue.createURI(uriStr.substring(0, pos))
    }
    result
  }

  def getLabel: String = {
    val uriStr: String = this.uri.toASCIIString
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    var result: String = ""
    if (pos == -1) {
      result = ""
    } else {
      result = uriStr.substring(pos + SpecialSymbol.length())
    }
    result
  }

  override def isEmpty: Boolean = {
    getUri.toASCIIString.trim().isEmpty
  }

  override def render(): String = {
    this.uri.toASCIIString
  }

  override def renderAsList(): Seq[String] = {
    val list = new mutable.ArrayBuffer[String]()
    list += render()
    val result: Seq[String] = list.toList
    result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    this.uri.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: URIValue => result = getUri.equals(other.getUri)
      case _ => result = false
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
    var result: URI = URI.create("")
    try {
      result = new URI(uriStr)
    } catch {
      case e: URISyntaxException => throw new ParseException("Invalid URI '" + uriStr + "'.", e)
    }
    result
  }

}
