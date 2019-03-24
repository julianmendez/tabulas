package de.tudresden.inf.lat.tabulas.table

import java.net.URI

import scala.collection.mutable

/** An object of this class is a map of URI prefixes.
  * This implementation iterates on the keys keeping the order in which they were added for the first time.
  *
  */
case class PrefixMapImpl(prefixMap :Map[URI, URI], keyList : Seq[URI]) extends PrefixMap {

  override def isEmpty: Boolean = {
    prefixMap.isEmpty
  }

  override def size(): Int = {
    prefixMap.size
  }

  override def getWithoutPrefix(uri: URI): URI = {
    var result = uri
    val uriStr = uri.toASCIIString
    if (uriStr.startsWith(PrefixAmpersand)) {
      val pos = uriStr.indexOf(PrefixSemicolon, PrefixAmpersand.length())
      if (pos != -1) {
        val prefix: URI = URI.create(uriStr.substring(PrefixAmpersand.length(), pos))
        val optExpansion: Option[URI] = prefixMap.get(prefix)
        if (optExpansion.isDefined) {
          result = URI.create(optExpansion.get.toASCIIString + uriStr.substring(pos + PrefixSemicolon.length))
        }
      }
    }
    result
  }

  override def getWithPrefix(uri: URI): URI = {
    val optPrefix: Option[URI] = getPrefixFor(uri)
    val result = if (optPrefix.isDefined) {
      val uriStr = uri.toASCIIString
      val key: URI = optPrefix.get
      val keyStr: String = key.toASCIIString
      val optExpansion: Option[URI] = get(key)
      val expansionStr = optExpansion.get.toASCIIString
      val res = if (keyStr.isEmpty) {
        URI.create(uriStr.substring(expansionStr.length))
      } else {
        URI.create(PrefixAmpersand + keyStr + PrefixSemicolon + uriStr.substring(expansionStr.length))
      }
      res
    } else {
      uri
    }
    result
  }

  override def get(key: URI): Option[URI] = {
    prefixMap.get(key)
  }

  override def getPrefixFor(uri: URI): Option[URI] = {
    val uriStr = uri.toASCIIString
    val result: Option[URI] = prefixMap.keySet.find(e => uriStr.startsWith(prefixMap.get(e).get.toASCIIString))
    result
  }

  override def getKeysAsStream: Stream[URI] = {
    keyList.toStream
  }

  override def toString: String = {
    prefixMap.toString + " " + keyList.toString
  }

}

