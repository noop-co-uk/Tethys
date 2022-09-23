package uk.co.noop.tethys.metis;

import uk.co.noop.tethys.enumeration.Method;
import uk.co.noop.themis.Themis;

public class GetMetis<D> extends AbstractMetis<D> {

  public static <DD> GetMetisBuilder<DD> builder(final Class<DD> dataType) {

    Themis.validate("dataType", dataType).againstNullObjects();

    return new GetMetisBuilder<DD>().dataType(dataType);
  }

  private GetMetis() {
    super();
  }

  public static class GetMetisBuilder<D>
      extends AbstractMetis.AbstractMetisBuilder<
          GetMetis<D>,
          GetMetisBuilder<D>,
          D> {

    private GetMetisBuilder() {
      super();
      method(Method.GET);
    }

    @Override
    protected GetMetis<D> getMetis() {
      return new GetMetis<>();
    }

    @Override
    protected GetMetisBuilder<D> getBuilder() {
      return this;
    }

  }

}
