
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.table.TableMap

/** Renderer.
  *
  */
trait Renderer {

  def render(writer:Writer, table: TableMap): Unit

}

