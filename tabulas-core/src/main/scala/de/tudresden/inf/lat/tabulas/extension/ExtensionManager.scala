package de.tudresden.inf.lat.tabulas.extension

import java.io.{IOException, UncheckedIOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.ParseException

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** This models an extension that can execute other extensions.
  *
  */
class ExtensionManager extends Extension {

  val Name: String = "ext"
  val Help: String = "extension manager"
  val RequiredArguments: Int = 1
  val NewLine: Char = '\n'
  val Space: Char = ' '

  private val _extensions: mutable.Buffer[Extension] = new ArrayBuffer[Extension]()
  private val _extensionMap: mutable.Map[String, Extension] = new mutable.TreeMap[String, Extension]()

  /** Constructs an extension manager.
    *
    * @param extensions
    * list of extensions
    */
  def this(extensions: mutable.Buffer[Extension]) = {
    this()
    if (Objects.nonNull(extensions)) {
      this._extensions ++= extensions
      extensions.foreach(extension => {
        val key: String = extension.getExtensionName
        if (this._extensionMap.get(key).isDefined) {
          throw new ExtensionException(
            "Only one implementation is allowed for each extension, and '"
              + key + "' was at least twice.")
        }
        this._extensionMap.put(key, extension)
      })
    }
  }

  override def process(arguments: mutable.Buffer[String]): Boolean = {
    Objects.requireNonNull(arguments)
    if (arguments.size < RequiredArguments) {
      throw new ExtensionException("No extension name was given.")
    } else {
      val command: String = arguments(0)
      val newArguments: mutable.Buffer[String] = new ArrayBuffer[String]()
      newArguments ++= arguments
      newArguments.remove(0)
      val optExtension: Option[Extension] = this._extensionMap.get(command)
      if (optExtension.isEmpty) {
        throw new ExtensionException("Extension '" + command
          + "' was not found.")
      } else if (newArguments.size < optExtension.get.getRequiredArguments) {
        throw new ExtensionException("Insufficient number of arguments for extension '" + command + "'.")
      } else {
        try {
          return optExtension.get.process(newArguments)
        } catch {
          case e@(_: ParseException | _: UncheckedIOException | _: IOException) =>
            throw new ExtensionException(e.toString, e)
        }
      }
    }
  }

  override def getExtensionName: String = {
    return Name
  }

  override def getHelp: String = {
    val sbuf: StringBuffer = new StringBuffer()
    this._extensions.foreach(extension => {
      sbuf.append(extension.getExtensionName)
      sbuf.append(Space)
      sbuf.append(extension.getHelp)
      sbuf.append(NewLine)
    })
    return sbuf.toString
  }

  override def getRequiredArguments: Int = {
    return RequiredArguments
  }

}
