package uk.co.noop.tethys.metis;

import uk.co.noop.tethys.enumeration.Method;
import uk.co.noop.themis.Themis;

import java.util.Optional;

public class PostMetis<R> extends AbstractMetis<R> {

  private Object body;

  public static <RR> PostMetisBuilder<RR> builder(
      final Class<RR> responseType) {

    Themis.validate("responseType", responseType).againstNullObjects();

    return new PostMetisBuilder<RR>().dataType(responseType);
  }

  private PostMetis() {
    super();
  }

  private void setBody(final Object body) {

    Themis.validate("body", body).againstNullObjects();

    this.body = body;
  }

  protected Optional<Object> getOutgoingData() {
    return Optional.ofNullable(body);
  }

  public static class PostMetisBuilder<R>
      extends AbstractMetis.AbstractMetisBuilder<
          PostMetis<R>,
          PostMetisBuilder<R>,
          R> {

    private Object body;

    private PostMetisBuilder() {
      super();
      method(Method.POST);
    }

    @Override
    protected PostMetis<R> getMetis() {

      final PostMetis<R> metis = new PostMetis<>();

      Optional.ofNullable(body).ifPresent(metis::setBody);

      return metis;
    }

    public PostMetisBuilder<R> body(final Object body) {
      this.body = body;
      return this;
    }

    @Override
    protected PostMetisBuilder<R> getBuilder() {
      return this;
    }

  }

}
