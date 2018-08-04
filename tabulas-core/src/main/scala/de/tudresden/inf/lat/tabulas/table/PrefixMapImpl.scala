package de.tudresden.inf.lat.tabulas.table

import java.net.URI

import scala.collection.mutable

/** An object of this class is a map of URI prefixes.
  * This implementation iterates on the keys keeping the order in which they were added for the first time.
  *
  */
class PrefixMapImpl extends PrefixMap {

  private val _prefixMap = new mutable.TreeMap[URI, URI]
  private val _keyList = new mutable.ArrayBuffer[URI]

  override def isEmpty: Boolean = {
    this._prefixMap.isEmpty
  }

  override def size(): Int = {
    this._prefixMap.size
  }

  override def get(key: URI): Option[URI] = {
    this._prefixMap.get(key)
  }

  override def put(key: URI, value: URI): Option[URI] = {
    if (!this._prefixMap.contains(key)) {
      this._keyList += key
    }
    val result: Option[URI] = this._prefixMap.put(key, value)
    result
  }

  override def getPrefixFor(uri: URI): Option[URI] = {
    val uriStr: String = uri.toASCIIString
    val result: Option[URI] = _prefixMap.keySet.find(e => uriStr.startsWith(_prefixMap.get(e).get.toASCIIString))
    result
  }

  override def getWithoutPrefix(uri: URI): URI = {
    var result: URI = uri
    val uriStr = uri.toASCIIString
    if (uriStr.startsWith(PrefixAmpersand)) {
      val pos = uriStr.indexOf(PrefixSemicolon, PrefixAmpersand.length())
      if (pos != -1) {
        val prefix: URI = URI.create(uriStr.substring(PrefixAmpersand.length(), pos))
        val optExpansion: Option[URI] = this._prefixMap.get(prefix)
        if (optExpansion.isDefined) {
          result = URI.create(optExpansion.get.toASCIIString + uriStr.substring(pos + PrefixSemicolon.length))
        }
      }
    }
    result
  }

  override def getWithPrefix(uri: URI): URI = {
    var result: URI = uri
    val optPrefix: Option[URI] = getPrefixFor(uri)
    if (optPrefix.isDefined) {
      val uriStr = uri.toASCIIString
      val key: URI = optPrefix.get
      val keyStr: String = key.toASCIIString
      val optExpansion: Option[URI] = get(key)
      val expansionStr = optExpansion.get.toASCIIString
      if (keyStr.isEmpty) {
        result = URI.create(uriStr.substring(expansionStr.length))
      } else {
        result = URI.create(PrefixAmpersand + keyStr + PrefixSemicolon + uriStr.substring(expansionStr.length))
      }
    }
    result
  }

  override def getKeysAsStream: Stream[URI] = {
    this._keyList.toStream
  }

  override def clear(): Unit = {
    this._prefixMap.clear()
    this._keyList.clear()
  }

}

object PrefixMapImpl {

  def apply(): PrefixMapImpl = new PrefixMapImpl

}
