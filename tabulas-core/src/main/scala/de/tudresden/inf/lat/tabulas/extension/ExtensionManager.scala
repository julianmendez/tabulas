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

  private val _extensionMap = getExtensionMap(extensions)

  def getExtensionNames: Set[String] = _extensionMap.keySet

  /** Returns the extension map.
    *
    * @param extensions list of extensions
    */
  def getExtensionMap(extensions: Seq[Extension]): Map[String, Extension] = {
    extensions.map(extension => (extension.getExtensionName, extension)).toMap
  }

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    Objects.requireNonNull(arguments)
    val result = if (arguments.size < RequiredArguments) {
      throw ExtensionException("No extension name was given.")

    } else {
      val command = arguments(0)
      val newArguments = new mutable.ArrayBuffer[String]()
      newArguments ++= arguments
      newArguments.remove(0)
      val optExtension: Option[Extension] = this._extensionMap.get(command)
      if (optExtension.isEmpty) {
        throw ExtensionException("Extension '" + command + "' was not found.")

      } else if (newArguments.size < optExtension.get.getRequiredArguments) {
        throw ExtensionException("Insufficient number of arguments for extension '" + command + "'.")

      } else {
        try {
          optExtension.get.process(newArguments).get

        } catch {
          case e@(_: ParseException | _: UncheckedIOException | _: IOException) =>
            throw ExtensionException(e.toString, e)
        }
      }
    }
    result
  }

  override def getExtensionName: String = {
    Name
  }

  override def getHelp: String = {
    val result = extensions.map(extension => {
      extension.getExtensionName + Space + extension.getHelp
    }).mkString("" + NewLine)
    result
  }

  override def getRequiredArguments: Int = {
    RequiredArguments
  }

}
