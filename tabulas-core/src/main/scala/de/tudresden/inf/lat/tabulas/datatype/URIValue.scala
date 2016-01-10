
package de.tudresden.inf.lat.tabulas.datatype

import java.net.URI
import java.net.URISyntaxException
import java.util.ArrayList
import java.util.Collections
import java.util.List

/**
 * This models a URI.
 *
 */
class URIValue extends PrimitiveTypeValue {

  val SpecialSymbol: String = "#"

  private var uri: URI = null

  /**
   * Constructs a new URI value using a string.
   *
   * @param link
   *            URI
   */
  def this(link: String) = {
    this()
    this.uri = createURI(link)
  }

  /**
   * Constructs a new URI value using another URI value.
   *
   * @param other
   *            URI value
   */
  def this(other: URIValue) {
    this()
    this.uri = other.getUri()
  }

  override def getType(): PrimitiveType = {
    new URIType()
  }

  def createURI(uri0: String): URI = {
    try {
      new URI(uri0)
    } catch {
      case e: URISyntaxException => {
        throw new ParseException("Invalid URI '" + uri0 + "'.", e)
      }
    }
  }

  def getUri(): URI = {
    this.uri
  }

  def getUriNoLabel(): URI = {
    val uriStr: String = this.uri.toASCIIString()
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol)
    if (pos == -1) {
      this.uri
    } else {
      createURI(uriStr.substring(0, pos))
    }
  }

  def getLabel(): String = {
    val uriStr: String = this.uri.toASCIIString()
    val pos: Int = uriStr.lastIndexOf(SpecialSymbol);
    if (pos == -1) {
      ""
    } else {
      uriStr.substring(pos + SpecialSymbol.length())
    }
  }

  override def isEmpty(): Boolean = {
    (getUri() == null) || (getUri().toASCIIString().trim().isEmpty())
  }

  override def render(): String = {
    this.uri.toASCIIString()
  }

  override def renderAsList(): List[String] = {
    val ret: List[String] = new ArrayList[String]()
    ret.add(render())
    Collections.unmodifiableList(ret)
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString().compareTo(other.toString())
  }

  override def hashCode(): Int = {
    this.uri.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    } else if (!(obj.isInstanceOf[URIValue])) {
      false
    } else {
      val other: URIValue = obj.asInstanceOf[URIValue];
      getUri().equals(other.getUri())
    }
  }

  override def toString(): String = {
    render()
  }

}

