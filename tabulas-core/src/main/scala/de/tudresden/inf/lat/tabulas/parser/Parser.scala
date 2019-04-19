
package de.tudresden.inf.lat.tabulas.parser

import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

/** Parser.
  *
  */
trait Parser {

  def parse(): Try[TableMap]

}

