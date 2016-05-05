
package de.tudresden.inf.lat.tabulas.renderer

import java.util.List

import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * Record renderer.
 *
 */
trait RecordRenderer {

  def render(record: Record, fields: List[String]): Unit

}

