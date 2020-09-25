
package de.tudresden.inf.lat.tabulas.parser

import java.io.Reader

import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

/** Parser.
 *
 */
trait Parser {

  def parse(reader: Reader): Try[TableMap]

}

