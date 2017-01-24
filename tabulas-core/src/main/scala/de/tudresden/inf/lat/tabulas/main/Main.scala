package de.tudresden.inf.lat.tabulas.main

import java.util.ArrayList
import java.util.List

import de.tudresden.inf.lat.tabulas.extension.DefaultExtension
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.extension.NormalizationExtension

/**
 * This is the main class.
 */
object Main {

	/**
	 * Entry point for the console.
	 * 
	 * @param args
	 *            console arguments
	 */
  def main(args: Array[String]): Unit = {
    val extensions: List[Extension] = new ArrayList[Extension]()
    extensions.add(new DefaultExtension())
    extensions.add(new NormalizationExtension())

    val instance: ConsoleStarter = new ConsoleStarter()
    instance.run(extensions, args)
  }

}
