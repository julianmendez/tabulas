
package de.tudresden.inf.lat.tabulas.datatype

import java.net.{URI, URISyntaxException}
import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * This models a URI.
  *
  */
class URIValue extends PrimitiveTypeValue {

  val SpecialSymbol: String = "#"

  private var uri: URI = _

  /**
    * Constructs a new URI value using a string.
    *
    * @param uriStr
    * URI
    */
  def this(uriStr: String) = {
    this()
    Objects.requireNonNull(uriStr)
    this.uri = createURI(uriStr)
  }

  /**
    * Constructs a new URI value using a URI.
    *
    * @param uri
    * URI
    */
  def this(uri: URI) = {
    this()
    Objects.requireNonNull(uri)
    this.uri = uri
  }

  /**
    * Constructs a new URI value using another URI value.
    *
    * @param other
    * URI value
    */
  def this(other: URIValue) {
    this()
    this.uri = other.getUri
  }

  override def getType: PrimitiveType = {
    return new URIType()
  }

  def createURI(uriStr: String): URI = {
    var result: URI = URI.create("")
    try {
      result = new URI(uriStr)
    } catch {
      case e: URISyntaxException => {
        throw new ParseException("Invalid URI '" + uriStr + "'.", e)
      }
    }

    return result
  }

  def getUri: URI = {
    return this.uri
  }

  def getUriNoLabel: URI = {
    val uriStr: String = this.uri.toASCIIString
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    var result: URI = this.uri
    if (pos == -1) {
      result = this.uri
    } else {
      result = createURI(uriStr.substring(0, pos))
    }

    return result
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

    return result
  }

  override def isEmpty: Boolean = {
    return getUri.toASCIIString.trim().isEmpty
  }

  override def render(): String = {
    return this.uri.toASCIIString
  }

  override def renderAsList(): mutable.Buffer[String] = {
    val list: mutable.Buffer[String] = new ArrayBuffer[String]()
    list += render()
    val result: mutable.Buffer[String] = list // @FIXME this should be immutable

    return result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    return toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    return this.uri.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: URIValue => result = getUri.equals(other.getUri)
      case _ => result = false
    }

    return result
  }

  override def toString: String = {
    return render()
  }

}

