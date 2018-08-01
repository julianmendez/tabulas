
package de.tudresden.inf.lat.tabulas.renderer

import de.tudresden.inf.lat.tabulas.datatype.Record

/** Record renderer.
  *
  */
trait RecordRenderer {

  def render(record: Record, fields: Seq[String]): Unit

}

