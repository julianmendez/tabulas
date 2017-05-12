
package de.tudresden.inf.lat.tabulas.renderer

import de.tudresden.inf.lat.tabulas.table.TableMap

/**
  * Renderer.
  *
  */
trait Renderer {

  def render(table: TableMap): Unit

}

