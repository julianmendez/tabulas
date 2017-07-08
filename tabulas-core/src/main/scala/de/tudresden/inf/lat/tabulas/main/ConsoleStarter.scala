package de.tudresden.inf.lat.tabulas.main

import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.{Extension, ExtensionException, ExtensionManager, NormalizationExtension}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * An object of this class runs the application with the given arguments.
  */
class ConsoleStarter {

  private val ErrorPrefix: String = "ERROR: "

  private var help: String = "\nusage: java -jar (jarname) (extension) (input) (output)\n" + //
    "\nIf the extension is ommitted, the '" + (new NormalizationExtension()).Name + "' extension is executed." + //
    "\n\nThe available extensions are:" + "\n"

  var manager: ExtensionManager = _

  /**
    * Constructs a new console starter.
    *
    * @param help
    * help about usage
    */
  def this(help: String) = {
    this()
    this.help = help
  }

  /**
    * Executes the application
    *
    * @param extensions
    * extensions
    * @param args
    * console arguments
    */
  def run(extensions: mutable.Buffer[Extension], args: Array[String]): Unit = {
    Objects.requireNonNull(extensions)
    Objects.requireNonNull(args)

    val arguments: mutable.Buffer[String] = new ArrayBuffer[String]()
    if (args.length == 1) {
      arguments += (new NormalizationExtension()).Name
    }
    arguments ++= args.toList

    val manager: ExtensionManager = new ExtensionManager(extensions)
    try {
      manager.process(arguments)
    } catch {
      case e: ExtensionException => {
        System.out.println(ErrorPrefix + e.getMessage)
        System.out.println(help + manager.getHelp)
      }
    }
  }

}
