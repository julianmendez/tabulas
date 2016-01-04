package de.tudresden.inf.lat.tabulas.extension

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.TreeMap

import scala.collection.JavaConversions.asScalaBuffer

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

  val extensions: List[Extension] = new ArrayList[Extension]()
  val extensionMap: Map[String, Extension] = new TreeMap[String, Extension]()

  /**
   * Constructs an extension manager.
   *
   * @param extensions
   *            list of extensions
   */
  def this(extensions: List[Extension]) = {
    this()
    if (extensions != null) {
      this.extensions.addAll(extensions)
      extensions.foreach { extension =>
        val key: String = extension.getExtensionName()
        if (this.extensionMap.containsKey(key)) {
          throw new ExtensionException(
            "Only one implementation is allowed for each extension, and '"
              + key + "' was at least twice.")
        }
        this.extensionMap.put(key, extension)
      }
    }
  }

  override def process(arguments: List[String]): Boolean = {
    if (arguments == null || arguments.size() < RequiredArguments) {
      false
    } else {
      val command: String = arguments.get(0)
      val newArguments: List[String] = new ArrayList[String]()
      newArguments.addAll(arguments)
      newArguments.remove(0)
      val extension: Extension = this.extensionMap.get(command)
      if (extension == null) {
        throw new ExtensionException("Extension '" + command
          + "' was not found.")
      } else {
        extension.process(newArguments)
        true
      }
    }
  }

  override def getExtensionName(): String = {
    Name
  }

  override def getHelp(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    this.extensions.foreach { extension =>
      sbuf.append(extension.getExtensionName())
      sbuf.append(Space)
      sbuf.append(extension.getHelp())
      sbuf.append(NewLine)
    }
    sbuf.toString()
  }

  override def getRequiredArguments(): Int = {
    RequiredArguments
  }

}
