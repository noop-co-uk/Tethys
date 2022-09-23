package uk.co.noop.tethys.exception;

import java.io.IOException;

public class TethysIOException extends TethysException {

  private static final String MESSAGE = "Tethys caught an IOException.";

  public TethysIOException(final IOException cause) {
    super(MESSAGE, cause);
  }

}
