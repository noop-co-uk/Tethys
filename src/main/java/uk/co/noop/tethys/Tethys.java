package uk.co.noop.tethys;

import uk.co.noop.tethys.exception.TethysException;
import uk.co.noop.tethys.metis.GetMetis;
import uk.co.noop.tethys.metis.PostMetis;

import java.util.Optional;

public class Tethys {

  public static <D> GetMetis.GetMetisBuilder<D> getBuilder(
      final String url,
      final Class<D> dataType) {

    return GetMetis.builder(dataType).url(url);
  }

  public static <D> Optional<D> get(final String url, final Class<D> dataType)
      throws TethysException {

    return getBuilder(url, dataType)
        .send()
        .errorForUnsuccessfulStatus()
        .getData();
  }

  public static <D> PostMetis.PostMetisBuilder<D> postBuilder(
      final String url,
      final Class<D> dataType) {

    return PostMetis.builder(dataType).url(url);
  }

  public static <D> Optional<D> post(
      final String url,
      final Object body,
      final Class<D> dataType) throws TethysException {

    return postBuilder(url, dataType)
        .body(body)
        .send()
        .errorForUnsuccessfulStatus()
        .getData();
  }

  public static <D> Optional<D> post(final String url, final Class<D> dataType)
      throws TethysException {

    return post(url, null, dataType);
  }

}
