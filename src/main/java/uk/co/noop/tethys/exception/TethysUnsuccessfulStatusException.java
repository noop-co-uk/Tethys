package uk.co.noop.tethys.exception;

public class TethysUnsuccessfulStatusException extends TethysException {

  private static final String MESSAGE_FORMAT =
      "Tethys received an unsuccessful status; `%d`.";

  public TethysUnsuccessfulStatusException(final int status) {
    super(String.format(MESSAGE_FORMAT, status));
  }

}
