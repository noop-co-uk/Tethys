package uk.co.noop.tethys.metis.result;

import uk.co.noop.tethys.exception.TethysUnsuccessfulStatusException;
import uk.co.noop.themis.Themis;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MetisResult<D> {

  private Integer status;
  private D data;

  private static final List<Integer> SUCCESSFUL_STATUSES = Arrays.asList(
      200, // OK
      201, // Created
      202, // Accepted
      203, // Non-Authoritative Information
      204, // No Content
      205, // Reset Content
      206  // Partial Content
  );

  public static <D> MetisResultBuilder<D> builder(final Class<D> dataType) {

    Themis.validate("dataType", dataType).againstNullObjects();

    return new MetisResultBuilder<>();
  }

  private MetisResult() {
    super();
  }

  private void setStatus(final Integer status) {

    Themis.validate("status", status);

    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  private void setData(final D data) {

    Themis.validate("data", data).againstNullObjects();

    this.data = data;
  }

  public Optional<D> getData() {
    return Optional.ofNullable(data);
  }

  public MetisResult<D> errorForUnsuccessfulStatus()
      throws TethysUnsuccessfulStatusException {

    if (!SUCCESSFUL_STATUSES.contains(status)) {
      throw new TethysUnsuccessfulStatusException(status);
    }

    return this;
  }

  public static class MetisResultBuilder<D> {

    private Integer status;
    private D data;

    private MetisResultBuilder() {
      super();
    }

    public MetisResultBuilder<D> status(final Integer status) {
      this.status = status;
      return this;
    }

    public MetisResultBuilder<D> data(final D data) {
      this.data = data;
      return this;
    }

    public MetisResult<D> build() {

      final MetisResult<D> result = new MetisResult<>();

      result.setStatus(status);
      Optional.ofNullable(data).ifPresent(result::setData);

      return result;
    }

  }

}
