package ke.co.shambapay.utils

import androidx.annotation.CheckResult
import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.io.Reader
import java.lang.NullPointerException
import java.lang.StringBuilder
import java.util.ArrayList
import kotlin.Throws

/**
 * Reads TAB delimited files.
 * Supports quoted cells and the main platforms' line endings: Unix `\n`, Mac `\r`, Windows `\r\n`.
 */
class TSVReader(reader: Reader) : Closeable {
    private val columns: MutableList<String> = ArrayList()
    private val parser: TSVParser

    /**
     * Parses and returns the cells from the next row in the input.
     *
     * @return cells as an array for the current row, quotes are removed around quoted cells and content unescaped.
     * `null` is returned when there's no more rows.
     * @throws IOException    if read fails
     * @throws ParseException if the read contents are invalid (e.g. unfinished quoted cell)
     */
    @Throws(IOException::class)
    fun readRow(): Array<String>? {
        if (parser.isEndOfFile) {
            return null
        }
        if (parser.isEndOfLine) {
            parser.consumeEndOfLine()
            return arrayOf("")
        }
        columns.clear()
        while (!parser.isEndOfLine) {
            columns.add(parser.readCell())
        }
        parser.consumeEndOfLine()
        return columns.toTypedArray()
    }

    @Throws(IOException::class)
    override fun close() {
        parser.close()
    }

    /**
     * Custom exception thrown for TSV format problems.
     */
    class ParseException internal constructor(message: String) : IOException(message)
    private class TSVParser internal constructor(reader: Reader) : Closeable {
        private val reader: Reader

        /**
         * Wraps the given [Reader] to support peeking.
         */
        private fun buffer(reader: Reader): Reader {
            var reader = reader
            if (!reader.markSupported()) {
                reader = BufferedReader(reader)
            }
            return reader
        }

        @CheckResult
        @Throws(IOException::class)
        private fun consumeSingleCharacter(): Int {
            return reader.read()
        }

        @Throws(IOException::class)
        private fun skipNextCharacter() {
            val consumed = consumeSingleCharacter()
            assert(-1 != consumed)
        }

        @CheckResult
        @Throws(IOException::class)
        private fun peekNextCharacter(): Int {
            reader.mark(1)
            val value = consumeSingleCharacter()
            reader.reset()
            return value
        }

        @Throws(IOException::class)
        override fun close() {
            reader.close()
        }

        private fun isEOF(chr: Int): Boolean {
            return chr == -1
        }

        private fun isEOL(chr: Int): Boolean {
            return chr == '\n'.toInt() || chr == '\r'.toInt() || isEOF(chr)
        }

        @get:Throws(IOException::class)
        val isEndOfFile: Boolean
            get() = isEOF(peekNextCharacter())

        @get:Throws(IOException::class)
        val isEndOfLine: Boolean
            get() = isEOL(peekNextCharacter())

        /**
         * Reads and hence consumes a platform independent end of line. Idempotent behavior at the end of file.
         *
         * @throws IOException    if reading fails
         * @throws ParseException if there's no end of line at this point
         */
        @Throws(IOException::class)
        fun consumeEndOfLine() {
            val chr = consumeSingleCharacter()
            if (!isEOL(chr)) {
                throw ParseException("Parser is not at the end of a line.")
            }
            if (chr == '\r'.toInt() && peekNextCharacter() == '\n'.toInt()) {
                skipNextCharacter() // \n of Windows \r\n
            }
            // Unix \n and Mac \r already read into chr
        }

        /**
         * Reads a cell that from the reader.
         * After this, the reader will be positioned at the beginning of the next cell or on the line terminator.
         *
         * @return cell contents, unescaped
         */
        @Throws(IOException::class)
        fun readCell(): String {
            val quoted = peekNextCharacter() == QUOTE.toInt()
            return if (quoted) {
                readQuotedCell()
            } else {
                readRawCell()
            }
        }

        /**
         * Reads a cell that has quotes around it from the reader.
         * After this, the reader will be positioned at the beginning of the next cell or on the line terminator.
         *
         * @return cell contents, with quotes around removed, and quotes inside unescaped
         * @throws IOException    if reading fails
         * @throws ParseException if there's a syntax error: cell must start and end with a quote
         */
        @Throws(IOException::class)
        private fun readQuotedCell(): String {
            val cell = StringBuilder()
            if (consumeSingleCharacter() != QUOTE.toInt()) {
                throw ParseException("Quoted cell must start with a quote.")
            }
            var terminated = false
            while (!isEndOfFile) {
                var chr = consumeSingleCharacter()
                if (chr == QUOTE.toInt()) {
                    chr = peekNextCharacter()
                    if (chr == DELIMITER.toInt()) {
                        skipNextCharacter() // consume delimiter
                        terminated = true
                        break // end of cell
                    } else if (chr == QUOTE.toInt()) {
                        skipNextCharacter() // consume quote
                        // found an escaped quote that is part of the cell
                        cell.append(chr.toChar())
                        continue  // continue parsing with next
                    } else if (isEOL(chr)) {
                        // don't consume end of line
                        terminated = true
                        break // end of cell
                    }
                    throw ParseException(
                        "Invalid input: quote must be escaped, terminating a cell or terminating the line."
                    )
                } else { // normal character
                    cell.append(chr.toChar())
                }
            }
            if (!terminated) {
                throw ParseException("Invalid input: cell \"$cell\" has no terminating quote.")
            }
            return cell.toString()
        }

        /**
         * Reads a cell that has unescaped content.
         * After this, the reader will be positioned at the beginning of the next cell or on the line terminator.
         *
         * @return cell contents, unmodified
         * @throws IOException if reading fails
         */
        @Throws(IOException::class)
        private fun readRawCell(): String {
            val cell = StringBuilder()
            while (!isEndOfLine) {
                val chr = consumeSingleCharacter()
                if (chr == DELIMITER.toInt()) {
                    break
                }
                cell.append(chr.toChar())
            }
            return cell.toString()
        }

        companion object {
            private const val DELIMITER = '\t'
            private const val QUOTE = '\"'
        }

        init {
            if (reader == null) throw NullPointerException("reader")
            this.reader = buffer(reader)
        }
    }

    /**
     * Creates a TSV reader for the specified input.
     * Any reader will do, but [BufferedReader] may give a better performance.
     *
     * @param reader to read the characters from
     */
    init {
        parser = TSVParser(reader)
    }
}