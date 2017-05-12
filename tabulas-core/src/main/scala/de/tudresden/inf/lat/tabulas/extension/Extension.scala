package de.tudresden.inf.lat.tabulas.extension

import scala.collection.mutable.Buffer

/**
 * This models an extension.
 *
 */
trait Extension {

  /**
   * Executes an extension.
   * @param arguments arguments
   *
   * @return <code>true</code> if the extension was successfully executed
   */
  def process(arguments: Buffer[String]): Boolean

  /**
   * Returns a name for this extension.
   *
   * @return a name for this extension
   */
  def getExtensionName(): String

  /**
   * Returns a human-readable help of what this extension does.
   *
   * @return a human-readable help of what this extension does
   */
  def getHelp(): String

  /**
   * Returns the number of required arguments.
   *
   * @return the number of required arguments
   */
  def getRequiredArguments(): Int

}
