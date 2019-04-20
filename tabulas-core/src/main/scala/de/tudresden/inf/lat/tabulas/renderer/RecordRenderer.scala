
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.datatype.Record

/** Record renderer.
  *
  */
trait RecordRenderer {

  def render(writer: Writer, record: Record, fields: Seq[String]): Unit

}

