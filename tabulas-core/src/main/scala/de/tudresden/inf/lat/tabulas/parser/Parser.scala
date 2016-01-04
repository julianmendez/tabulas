
package de.tudresden.inf.lat.tabulas.parser

import java.io.Writer

import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Parser.
 *
 */
trait Parser {

  def parse(): TableMap

}

