
package de.tudresden.inf.lat.tabulas.parser

import de.tudresden.inf.lat.tabulas.table.TableMap

/** Parser.
  *
  */
trait Parser {

  def parse(): TableMap

}

