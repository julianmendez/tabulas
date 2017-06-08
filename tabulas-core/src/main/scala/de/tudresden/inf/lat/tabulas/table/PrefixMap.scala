package de.tudresden.inf.lat.tabulas.table

import java.net.URI

import scala.collection.mutable.Map

/**
  * This models a map of URI prefixes.
  */
trait PrefixMap {

  val PrefixAmpersand: String = "&"

  val PrefixSemicolon: String = ";"

  def get(key: URI): Option[URI]

  def put(key: URI, value: URI): Option[URI]

  def getWithPrefix(uri: URI): URI

  def getWithoutPrefix(uri: URI): URI

  def getPrefixFor(uri: URI): Option[URI]

  def asMap(): Map[URI, URI]

  def getKeysAsStream(): Stream[URI]

  def clear(): Unit

}
