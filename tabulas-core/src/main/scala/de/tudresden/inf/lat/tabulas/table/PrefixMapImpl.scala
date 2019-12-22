package de.tudresden.inf.lat.tabulas.table

import java.net.URI

/** An object of this class is a map of URI prefixes.
  * This implementation iterates on the keys keeping the order in which they were added for the first time.
  *
  */
case class PrefixMapImpl(prefixMap: Map[URI, URI], keyList: Seq[URI]) extends PrefixMap {

  override def isEmpty: Boolean = {
    prefixMap.isEmpty
  }

  override def size(): Int = {
    prefixMap.size
  }

  override def getWithoutPrefix(uri: URI): URI = {
    val uriStr = uri.toString
    val result = if (uriStr.startsWith(PrefixAmpersand)) {
      val pos = uriStr.indexOf(PrefixSemicolon, PrefixAmpersand.length())
      val res = if (pos == -1) {
        uri
      } else {
        val prefix = URI.create(uriStr.substring(PrefixAmpersand.length(), pos))
        val optExpansion = prefixMap.get(prefix)
        val value = if (optExpansion.isDefined) {
          URI.create(optExpansion.get.toString + uriStr.substring(pos + PrefixSemicolon.length))
        } else {
          uri
        }
        value
      }
      res
    } else {
      uri
    }
    result
  }

  override def getWithPrefix(uri: URI): URI = {
    val optPrefix = getPrefixFor(uri)
    val result = if (optPrefix.isDefined) {
      val uriStr = uri.toString
      val key = optPrefix.get
      val keyStr = key.toString
      val optExpansion = get(key)
      val expansionStr = optExpansion.get.toString
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
    val uriStr = uri.toString
    val result = keyList.find(e => uriStr.startsWith(prefixMap.get(e).get.toString))
    result
  }

  override def getKeysAsStream: Stream[URI] = {
    keyList.toStream
  }

  override def toString: String = {
    prefixMap.toString + " " + keyList.toString
  }

}

object PrefixMapImpl {

  def apply(): PrefixMapImpl = {
    PrefixMapImpl(Map(), Seq())
  }

}
