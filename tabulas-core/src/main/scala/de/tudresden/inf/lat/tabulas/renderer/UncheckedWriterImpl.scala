package de.tudresden.inf.lat.tabulas.renderer

import java.io.{IOException, UncheckedIOException, Writer}
import java.util.Objects

/**
  * This is the default implementation of {@link UncheckedWriter}.
  *
  * @author Julian Mendez
  *
  */
class UncheckedWriterImpl extends UncheckedWriter {

  var writer: Writer = _

  /**
    * Constructs a new unchecked writer.
    *
    * @param writer
    * writer
    */
  def this(writer: Writer) = {
    this()
    Objects.requireNonNull(writer)
    this.writer = writer
  }

  override def write(character: Int): Unit = {
    try {
      this.writer.write(character)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def write(charBuffer: Array[Char]): Unit = {
    try {
      this.writer.write(charBuffer)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def write(charBuffer: Array[Char], offset: Int, length: Int): Unit = {
    try {
      this.writer.write(charBuffer, offset, length)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def write(str: String): Unit = {
    try {
      this.writer.write(str)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def write(str: String, offset: Int, length: Int): Unit = {
    try {
      this.writer.write(str, offset, length)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def close(): Unit = {
    try {
      this.writer.close()
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def flush(): Unit = {
    try {
      this.writer.flush()
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
  }

  override def append(character: Char): UncheckedWriter = {
    try {
      this.writer.append(character)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
    return this
  }

  override def append(charSequence: CharSequence): UncheckedWriter = {
    try {
      this.writer.append(charSequence)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
    return this
  }

  override def append(charSequence: CharSequence, start: Int, end: Int): UncheckedWriter = {
    try {
      this.writer.append(charSequence, start, end)
    } catch {
      case e: IOException => {
        throw new UncheckedIOException(e)
      }
    }
    return this
  }

  override def asWriter(): Writer = {
    return this.writer
  }

  override def hashCode(): Int = {
    return this.writer.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[UncheckedWriter])) {
      return false
    } else {
      val other: UncheckedWriter = obj.asInstanceOf[UncheckedWriter]
      return asWriter().equals(other.asWriter())
    }
  }

  override def toString(): String = {
    return this.writer.toString()
  }

}
