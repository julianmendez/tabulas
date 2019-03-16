package de.tudresden.inf.lat.tabulas.table

import java.net.URI

/** This models a map of URI prefixes.
  */
trait PrefixMap {

  /** Start of the prefix.
    */
  final val PrefixAmpersand: String = "&"

  /** End of the prefix.
    */
  final val PrefixSemicolon: String = ";"

  /** Returns <code>true</code> if and only if this map does not contain associations.
    *
    * @return <code>true</code> if and only if this map does not contain associations
    */
  def isEmpty: Boolean

  /** Returns the size of this prefix map.
    *
    * @return the size of this prefix map
    */
  def size(): Int

  /** Returns the expansion for the given prefix.
    *
    * @param key the prefix
    * @return expansion for the given prefix
    */
  def get(key: URI): Option[URI]

  /** Assigns a prefix to an expansion.
    *
    * @param key   prefix
    * @param value expansion
    * @return an optional containing the previous value of the given key, or empty if there was no value for that key
    */
  def put(key: URI, value: URI): Option[URI]

  /** Returns a URI with the prefix, i.e. a shortened URI.
    *
    * @param uri URI
    * @return a URI with the prefix
    */
  def getWithPrefix(uri: URI): URI

  /** Returns a URI without the prefix, i.e. with the prefix already expanded.
    *
    * @param uri uri
    * @return a URI without the prefix
    */
  def getWithoutPrefix(uri: URI): URI

  /** Returns an optional with the prefix for the given URI, or empty if there is none.
    *
    * @param uri URI
    * @return an optional with the prefix for the given URI, or empty if there is none
    */
  def getPrefixFor(uri: URI): Option[URI]

  /** Returns a stream to iterate on the keys.
    *
    * @return a stream to iterate on the keys
    */
  def getKeysAsStream: Stream[URI]

  /** Clears the content.
    */
  def clear(): Unit

}
