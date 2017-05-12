
package de.tudresden.inf.lat.tabulas.renderer

import scala.collection.mutable

import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * Record renderer.
 *
 */
trait RecordRenderer {

  def render(record: Record, fields: mutable.Buffer[String]): Unit

}

