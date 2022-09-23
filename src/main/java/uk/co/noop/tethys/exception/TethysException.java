package uk.co.noop.tethys.exception;

public class TethysException extends Exception {

  protected TethysException(final String message, final Throwable cause) {
    super(message, cause);
  }

  protected TethysException(final String message) {
    super(message);
  }

}
