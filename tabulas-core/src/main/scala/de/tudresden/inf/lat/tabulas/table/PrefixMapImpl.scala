package de.tudresden.inf.lat.tabulas.table

import java.net.URI

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Map, TreeMap}

/**
  * An object of this class is a map of URI prefixes.
  * This implementation iterates on the keys keeping the order in which they were added for the first time.
  *
  */
class PrefixMapImpl extends PrefixMap {

  private val prefixMap: Map[URI, URI] = new TreeMap[URI, URI]
  private val keyList: mutable.Buffer[URI] = new ArrayBuffer[URI]

  override def isEmpty(): Boolean = {
    return this.prefixMap.isEmpty
  }

  override def size(): Int = {
    return this.prefixMap.size
  }

  override def get(key: URI): Option[URI] = {
    return this.prefixMap.get(key)
  }

  override def put(key: URI, value: URI): Option[URI] = {
    if (!this.prefixMap.contains(key)) {
      this.keyList += key
    }
    return this.prefixMap.put(key, value)
  }

  override def getPrefixFor(uri: URI): Option[URI] = {
    val uriStr: String = uri.toASCIIString()
    val key: Option[URI] = prefixMap.keySet.find(e => uriStr.startsWith(prefixMap.get(e).get.toASCIIString()))
    if (key.isDefined) {
      return Option.apply(key.get)
    } else {
      return Option.empty
    }
  }

  override def getWithoutPrefix(uri: URI): URI = {
    var ret: URI = uri
    val uriStr = uri.toASCIIString()
    if (uriStr.startsWith(PrefixAmpersand)) {
      val pos = uriStr.indexOf(PrefixSemicolon, PrefixAmpersand.length())
      if (pos != -1) {
        val prefix: URI = URI.create(uriStr.substring(PrefixAmpersand.length(), pos))
        val optExpansion: Option[URI] = this.prefixMap.get(prefix)
        if (optExpansion.isDefined) {
          ret = URI.create(optExpansion.get.toASCIIString() + uriStr.substring(pos + PrefixSemicolon.length))
        }
      }
    }
    return ret
  }

  override def getWithPrefix(uri: URI): URI = {
    var ret: URI = uri
    val optPrefix: Option[URI] = getPrefixFor(uri)
    if (optPrefix.isDefined) {
      val uriStr = uri.toASCIIString()
      val key: URI = optPrefix.get
      val keyStr: String = key.toASCIIString()
      val optExpansion: Option[URI] = get(key)
      val expansionStr = optExpansion.get.toASCIIString()
      if (keyStr.isEmpty()) {
        ret = URI.create(uriStr.substring(expansionStr.length))
      } else {
        ret = URI.create(PrefixAmpersand + keyStr + PrefixSemicolon + uriStr.substring(expansionStr.length))
      }
    }
    return ret
  }

  override def getKeysAsStream(): Stream[URI] = {
    return this.keyList.toStream
  }

  override def clear(): Unit = {
    this.prefixMap.clear()
    this.keyList.clear()
  }

}
