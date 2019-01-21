package org.tues.stefchog.polyglot.io;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;

import org.slf4j.Logger;

class OutputImpl implements Output {
  private final OutputWriter writer;

  OutputImpl(OutputWriter writer) {
    this.writer = writer;
  }

  @Override
  public void close() {
    writer.close();
  }

  @Override
  public void write(String content) {
    writer.write(content);
  }

  @Override
  public void writeLine(String content) {
    write(content + "\n");
  }

  @Override
  public void newLine() {
    write("\n");
  }

  private interface OutputWriter {
    void write(String content);
    void close();
  }

  /** An {@link OutputWriter} which writes to a logger. */
  static class LogWriter implements OutputWriter {
    private final Logger logger;

    LogWriter(Logger logger) {
      this.logger = logger;
    }

    @Override
    public void write(String content) {
      logger.info(content);
    }

    @Override
    public void close() {
      // Do nothing.
    }
  }

  /** An {@link OutputWriter} which writes to a stream. */
  static class PrintStreamWriter implements OutputWriter {
    private final PrintStream printStream;

    static PrintStreamWriter forStdout() {
      return PrintStreamWriter.forStream(System.out);
    }

    static PrintStreamWriter forStream(PrintStream printStream) {
      return new PrintStreamWriter(printStream);
    }

    static PrintStreamWriter forFile(Path path) {
      try {
        return new PrintStreamWriter(new PrintStream(path.toString()));
      } catch (FileNotFoundException e) {
        throw new IllegalArgumentException("Could not create writer for file: " + path, e);
      }
    }

    private PrintStreamWriter(PrintStream printStream) {
      this.printStream = printStream;
    }

    @Override
    public void write(String content) {
      printStream.print(content);
    }

    @Override
    public void close() {
      printStream.close();
    }
  }
}
