package de.tudresden.inf.lat.tabulas.extension

import java.io.{IOException, UncheckedIOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.ParseException

import scala.collection.mutable
import scala.util.Try

/** This models an extension that can execute other extensions.
 *
 */
case class ExtensionManager(extensions: Seq[Extension]) extends Extension {

  final val Name: String = "ext"
  final val Help: String = "extension manager"
  final val RequiredArguments: Int = 1
  final val NewLine: Char = '\n'
  final val Space: Char = ' '

  override val getExtensionName: String = Name

  override val getHelp: String = {
    val result = extensions.map(extension => {
      "" + NewLine + Space + Space + extension.getExtensionName + Space + extension.getHelp
    }).mkString("" + NewLine) + NewLine
    result
  }

  override val getRequiredArguments: Int = RequiredArguments

  val getExtensionMap: Map[String, Extension] = {
    extensions.map(extension => (extension.getExtensionName, extension)).toMap
  }

  val getExtensionNames: Set[String] = getExtensionMap.keySet

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    Objects.requireNonNull(arguments)
    val result = if (arguments.size < RequiredArguments) {
      throw ExtensionException("No extension name was given.")

    } else {
      val command = arguments(0)
      val newArguments = new mutable.ArrayBuffer[String]()
      newArguments ++= arguments
      newArguments.remove(0)
      val optExtension: Option[Extension] = getExtensionMap.get(command)
      if (optExtension.isEmpty) {
        throw ExtensionException("Extension '" + command + "' was not found.")

      } else if (newArguments.size < optExtension.get.getRequiredArguments) {
        throw ExtensionException("Insufficient number of arguments for extension '" + command + "'.")

      } else {
        try {
          optExtension.get.process(newArguments.toSeq).get

        } catch {
          case e@(_: ParseException | _: UncheckedIOException | _: IOException) =>
            throw ExtensionException(e.toString, e)
        }
      }
    }
    result
  }

}

object ExtensionManager {}

