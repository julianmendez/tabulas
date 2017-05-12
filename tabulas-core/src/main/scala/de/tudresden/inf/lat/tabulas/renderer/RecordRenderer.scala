
package de.tudresden.inf.lat.tabulas.renderer

import scala.collection.mutable.Buffer

import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * Record renderer.
 *
 */
trait RecordRenderer {

  def render(record: Record, fields: Buffer[String]): Unit

}

