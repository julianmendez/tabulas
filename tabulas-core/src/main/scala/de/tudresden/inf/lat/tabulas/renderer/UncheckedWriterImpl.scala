package de.tudresden.inf.lat.tabulas.renderer

import java.io.{IOException, UncheckedIOException, Writer}

/** This is the default implementation of UncheckedWriter.
  *
  * @author Julian Mendez
  *
  */
case class UncheckedWriterImpl(writer: Writer) extends UncheckedWriter {

  override def write(character: Int): Unit = {
    try {
      writer.write(character)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def write(charBuffer: Array[Char]): Unit = {
    try {
      writer.write(charBuffer)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def write(charBuffer: Array[Char], offset: Int, length: Int): Unit = {
    try {
      writer.write(charBuffer, offset, length)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def write(str: String): Unit = {
    try {
      writer.write(str)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def write(str: String, offset: Int, length: Int): Unit = {
    try {
      writer.write(str, offset, length)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def close(): Unit = {
    try {
      writer.close()
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def flush(): Unit = {
    try {
      writer.flush()
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
  }

  override def append(character: Char): UncheckedWriter = {
    try {
      writer.append(character)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
    this
  }

  override def append(charSequence: CharSequence): UncheckedWriter = {
    try {
      writer.append(charSequence)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
    this
  }

  override def append(charSequence: CharSequence, start: Int, end: Int): UncheckedWriter = {
    try {
      writer.append(charSequence, start, end)
    } catch {
      case e: IOException => throw new UncheckedIOException(e)
    }
    this
  }

  override def asWriter(): Writer = {
    writer
  }

  override def toString: String = {
    writer.toString
  }

}
