package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer

/**
 * This models an unchecked writer. This looks like a {@link java.io.Writer}, but throws
 * an {@link java.io.UncheckedIOException} where a <code>Writer</code> throws an
 * {@link java.io.IOException}.
 *
 * @author Julian Mendez
 *
 */
trait UncheckedWriter {

  /**
   * Writes a single character.
   *
   * @param character
   *            character
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def write(character: Int): Unit

  /**
   * Writes an array of characters.
   *
   * @param charBuffer
   *            array of characters
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def write(charBuffer: Array[Char]): Unit

  /**
   * Writes an array of characters.
   *
   * @param charBuffer
   *            array of characters
   * @param offset
   *            offset
   * @param length
   *            number of characters to write
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def write(charBuffer: Array[Char], offset: Int, length: Int): Unit

  /**
   * Writes a string.
   *
   * @param str
   *            string
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def write(str: String): Unit

  /**
   * Writes a string
   *
   * @param str
   *            string
   * @param offset
   *            offset
   * @param length
   *            number of characters to write
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def write(str: String, offset: Int, length: Int): Unit

  /**
   * Closes the stream, flushing it first.
   *
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def close(): Unit

  /**
   * Flushes the stream.
   *
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def flush(): Unit

  /**
   * Appends a character to this writer.
   *
   * @param character
   *            character
   * @return this writer
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def append(character: Char): UncheckedWriter

  /**
   * Appends a character sequence to this writer.
   *
   * @param charSequence
   *            character sequence
   * @return this writer
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def append(charSequence: CharSequence): UncheckedWriter

  /**
   * Appends a character subsequence to this writer.
   *
   * @param charSequence
   *            character sequence
   * @param start
   *            start of the sequence
   * @param end
   *            end of the sequence
   * @return this writer
   * @throws java.io.UncheckedIOException
   *             if something goes wrong with input/output
   */
  def append(charSequence: CharSequence, start: Int, end: Int): UncheckedWriter

  /**
   * Returns this as a writer.
   *
   * @return this as a writer
   */
  def asWriter(): Writer

}
