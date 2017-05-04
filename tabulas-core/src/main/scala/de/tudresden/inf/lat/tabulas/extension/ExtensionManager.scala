package de.tudresden.inf.lat.tabulas.extension

import java.io.UncheckedIOException
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import java.util.Map
import java.util.Objects
import java.util.TreeMap

import scala.collection.JavaConverters.asScalaBufferConverter

import de.tudresden.inf.lat.tabulas.datatype.ParseException
import java.io.IOException

/**
 * This models an extension that can execute other extensions.
 *
 */
class ExtensionManager extends Extension {

  val Name: String = "ext"
  val Help: String = "extension manager"
  val RequiredArguments: Int = 1
  val NewLine: Char = '\n'
  val Space: Char = ' '

  val extensions: Buffer[Extension] = new ArrayBuffer[Extension]()
  val extensionMap: Map[String, Extension] = new TreeMap[String, Extension]()

  /**
   * Constructs an extension manager.
   *
   * @param extensions
   *            list of extensions
   */
  def this(extensions: Buffer[Extension]) = {
    this()
    if (Objects.nonNull(extensions)) {
      this.extensions ++= extensions
      extensions.foreach(extension => {
        val key: String = extension.getExtensionName()
        if (this.extensionMap.containsKey(key)) {
          throw new ExtensionException(
            "Only one implementation is allowed for each extension, and '"
              + key + "' was at least twice.")
        }
        this.extensionMap.put(key, extension)
      })
    }
  }

  override def process(arguments: Buffer[String]): Boolean = {
    Objects.requireNonNull(arguments)
    if (arguments.size < RequiredArguments) {
      throw new ExtensionException("No extension name was given.")
    } else {
      val command: String = arguments(0)
      val newArguments: Buffer[String] = new ArrayBuffer[String]()
      newArguments ++= arguments
      newArguments.remove(0)
      val extension: Extension = this.extensionMap.get(command)
      if (Objects.isNull(extension)) {
        throw new ExtensionException("Extension '" + command
          + "' was not found.")
      } else if (newArguments.size < extension.getRequiredArguments()) {
        throw new ExtensionException("Insufficient number of arguments for extension '" + command + "'.")
      } else {
        try {
          return extension.process(newArguments)
        } catch {
          case e @ (_: ParseException | _: UncheckedIOException | _: IOException) => {
            throw new ExtensionException(e.toString(), e)
          }
        }
      }
    }
  }

  override def getExtensionName(): String = {
    return Name
  }

  override def getHelp(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    this.extensions.foreach(extension => {
      sbuf.append(extension.getExtensionName())
      sbuf.append(Space)
      sbuf.append(extension.getHelp())
      sbuf.append(NewLine)
    })
    return sbuf.toString()
  }

  override def getRequiredArguments(): Int = {
    return RequiredArguments
  }

}
