package de.tudresden.inf.lat.tabulas.main

import java.util.ArrayList

import de.tudresden.inf.lat.tabulas.extension.DefaultExtension
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.extension.ExtensionManager
import de.tudresden.inf.lat.tabulas.extension.NormalizationExtension

/**
 * This is the main class.
 */
object Main {

  val Header: String = "Use: java -jar (jarname) (command) (input) (output)\n\n"

  var manager: ExtensionManager = null

  /**
   * Constructs a new main class.
   */
  {
    val extensions: ArrayList[Extension] = new ArrayList[Extension]()
    extensions.add(new DefaultExtension())
    extensions.add(new NormalizationExtension())

    this.manager = new ExtensionManager(extensions)
  }

  def run(args: Array[String]): Unit = {
    if ((args != null) && ((args.length == 2) || (args.length == 3))) {
      val arguments: ArrayList[String] = new ArrayList[String]();
      for (index <- 0 to (args.length - 1)) {
        arguments.add(args(index))
      }
      this.manager.process(arguments)
    } else {
      System.out.println(Header + this.manager.getHelp())
    }
  }

  def main(args: Array[String]): Unit = {
    run(args)
  }

}