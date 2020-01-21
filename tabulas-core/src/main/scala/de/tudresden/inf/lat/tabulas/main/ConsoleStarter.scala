package de.tudresden.inf.lat.tabulas.main

import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.{Extension, ExtensionException, ExtensionManager}

/** An object of this class runs the application with the given arguments.
  */
case class ConsoleStarter() {

  private final val ErrorPrefix: String = "ERROR: "

  private final val help = "\nusage: java -jar (jarname) (extension) (input) (output)\n" + //
    "\n\nThe available extensions are:" + "\n"

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
        print(help + manager.getHelp + "\n")
    }
  }

  def getTitleAndVersion: String = {
    val packg = this.getClass.getPackage
    val name = Option(packg.getImplementationTitle).getOrElse("")
    val version = Option(packg.getImplementationVersion).getOrElse("")
    val result = (name + " " + version).trim
    result
  }

}

object ConsoleStarter {}
