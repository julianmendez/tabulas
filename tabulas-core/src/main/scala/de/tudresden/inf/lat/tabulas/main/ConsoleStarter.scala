package de.tudresden.inf.lat.tabulas.main

import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.{Extension, ExtensionException, ExtensionManager}
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

/** An object of this class runs the application with the given arguments.
 */
case class ConsoleStarter() {

  private final val ErrorPrefix: String = "ERROR: "

  private final val Help = "\nusage: java -jar (jarname) (extension) (input) (output)\n" +
    "\n\n" + ParserConstant.WarningDeprecationOfMultipleTables + "\n\n" +
    "\n\nThe available extensions are:\n"

  val getTitleAndVersion: String = {
    val packg = this.getClass.getPackage
    val name = Option(packg.getImplementationTitle).getOrElse("")
    val version = Option(packg.getImplementationVersion).getOrElse("")
    (name + " " + version).trim
  }

  /** Executes the application
   *
   * @param extensions extensions
   * @param args       console arguments
   */
  def run(extensions: Seq[Extension], args: Array[String]): Unit = {
    Objects.requireNonNull(extensions)
    Objects.requireNonNull(args)

    val manager = ExtensionManager(extensions)
    try {
      manager.process(args.toIndexedSeq).get

    } catch {
      case e: ExtensionException =>
        print(getTitleAndVersion + "\n")
        print(ErrorPrefix + e.getMessage + "\n")
        print(Help + manager.getHelp + "\n")
    }
  }

}

object ConsoleStarter {}

