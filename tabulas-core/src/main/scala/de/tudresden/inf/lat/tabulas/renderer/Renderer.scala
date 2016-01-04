
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Renderer.
 */
trait Renderer {

  def render(table: TableMap): Unit

}

