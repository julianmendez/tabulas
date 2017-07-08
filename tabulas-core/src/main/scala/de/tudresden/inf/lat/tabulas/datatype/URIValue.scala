
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
    try {
      return new URI(uriStr)
    } catch {
      case e: URISyntaxException => {
        throw new ParseException("Invalid URI '" + uriStr + "'.", e)
      }
    }
  }

  def getUri: URI = {
    return this.uri
  }

  def getUriNoLabel: URI = {
    val uriStr: String = this.uri.toASCIIString()
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    if (pos == -1) {
      return this.uri
    } else {
      return createURI(uriStr.substring(0, pos))
    }
  }

  def getLabel: String = {
    val uriStr: String = this.uri.toASCIIString()
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    if (pos == -1) {
      return ""
    } else {
      return uriStr.substring(pos + SpecialSymbol.length())
    }
  }

  override def isEmpty: Boolean = {
    return (getUri.toASCIIString.trim().isEmpty)
  }

  override def render(): String = {
    return this.uri.toASCIIString
  }

  override def renderAsList(): mutable.Buffer[String] = {
    val ret: mutable.Buffer[String] = new ArrayBuffer[String]()
    ret += render()
    return ret // @FIXME this should be immutable
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    return toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    return this.uri.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[URIValue])) {
      return false
    } else {
      val other: URIValue = obj.asInstanceOf[URIValue]
      return getUri.equals(other.getUri)
    }
  }

  override def toString: String = {
    return render()
  }

}

