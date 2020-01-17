package com.roberttisma.tools.intermediate_song_importer.exceptions;

import static java.lang.String.format;

public class ImporterException extends RuntimeException {

  public ImporterException() {}

  public ImporterException(String message) {
    super(message);
  }

  public ImporterException(String message, Throwable cause) {
    super(message, cause);
  }

  public ImporterException(Throwable cause) {
    super(cause);
  }

  public ImporterException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public static void checkImporter(boolean expression, String formattedString, Object... args) {
    if (!expression) {
      throw buildImporterException(formattedString, args);
    }
  }

  public static ImporterException buildImporterException(String formattedString, Object... args) {
    return new ImporterException(format(formattedString, args));
  }
}
