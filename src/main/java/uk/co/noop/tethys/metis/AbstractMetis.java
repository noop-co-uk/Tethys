package uk.co.noop.tethys.metis;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.noop.tethys.enumeration.Method;
import uk.co.noop.tethys.exception.TethysIOException;
import uk.co.noop.tethys.metis.result.MetisResult;
import uk.co.noop.tethys.utils.HttpUtils;
import uk.co.noop.themis.Themis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractMetis<D> {

  private Method method;
  private String url;
  private Class<D> dataType;

  private final Map<String, String> queryParameters;
  private final Map<String, String> headers;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  protected AbstractMetis() {
    super();
    queryParameters = new HashMap<>();
    headers = new HashMap<>();
  }

  protected void setMethod(final Method method) {

    Themis.validate("method", method).againstNullObjects();

    this.method = method;
  }

  protected void setUrl(final String url) {

    Themis.validate("url", url).againstBlankStrings();

    this.url = url;
  }

  protected void setQueryParameter(final String key, final String value) {

    Themis.validate("key", key).againstBlankStrings();
    Themis.validate("value", value).againstBlankStrings();

    queryParameters.put(key, value);
  }

  protected void setHeader(final String key, final String value) {

    Themis.validate("key", key).againstBlankStrings();
    Themis.validate("value", value).againstBlankStrings();

    headers.put(key, value);
  }

  protected void setDataType(final Class<D> dataType) {

    Themis.validate("dataType", dataType).againstNullObjects();

    this.dataType = dataType;
  }

  public MetisResult<D> send() throws TethysIOException {

    HttpURLConnection httpURLConnection = null;

    final int responseCode;
    final D data;

    try {

      String queryString =
          queryParameters.entrySet()
              .stream()
              .map(entry ->
                  String.format("%s=%s", entry.getKey(), entry.getValue()))
              .collect(Collectors.joining("&"));

      final URL url;

      if (queryString.isEmpty()) {
        url = new URL(this.url);
      } else {
        url = new URL(String.format("%s?%s", this.url, queryString));
      }

      httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod(method.toString());
      httpURLConnection.setRequestProperty("Content-Type", "application/json");
      httpURLConnection.setRequestProperty("Accept", "application/json");
      headers.forEach(httpURLConnection::setRequestProperty);
      httpURLConnection.setInstanceFollowRedirects(true);

      final Optional<Object> outgoingData = getOutgoingData();

      if (outgoingData.isPresent()) {

        HttpUtils.write(
            httpURLConnection,
            OBJECT_MAPPER.writeValueAsString(outgoingData.get()));
      }

      // This is when the HTTP request is actually sent
      responseCode = httpURLConnection.getResponseCode();

      data =  OBJECT_MAPPER.readValue(
          HttpUtils.readAllLines(httpURLConnection, responseCode),
          dataType);

    } catch (final IOException e) {
      throw new TethysIOException(e);
    } finally {

      if (Objects.nonNull(httpURLConnection)) {
        httpURLConnection.disconnect();
      }
    }

    return MetisResult.builder(dataType)
        .status(responseCode)
        .data(data)
        .build();
  }

  protected Optional<Object> getOutgoingData() {
    return Optional.empty();
  }

  protected static abstract class AbstractMetisBuilder<
      M extends AbstractMetis<D>,
      B extends AbstractMetisBuilder<M, B, D>,
      D> {

    private Method method;
    private String url;
    private Map<String, String> queryParameters;
    private Map<String, String> headers;
    private Class<D> dataType;

    protected AbstractMetisBuilder() {
      super();
    }

    protected B method(final Method method) {
      this.method = method;
      return getBuilder();
    }

    public B url(final String url) {
      this.url = url;
      return getBuilder();
    }

    public B queryParameter(final String key, final String value) {

      if (Objects.isNull(queryParameters)) {
        queryParameters = new HashMap<>();
      }
      queryParameters.put(key, value);

      return getBuilder();
    }

    public B queryParameter(final String key, final boolean value) {
      return queryParameter(key, Boolean.toString(value));
    }

    public B queryParameter(final String key, final int value) {
      return queryParameter(key, Integer.toString(value));
    }

    public B queryParameter(final String key, final double value) {
      return queryParameter(key, Double.toString(value));
    }

    public B queryParameter(final String key, final long value) {
      return queryParameter(key, Long.toString(value));
    }

    public B header(final String key, final String value) {

      if (Objects.isNull(headers)) {
        headers = new HashMap<>();
      }
      headers.put(key, value);

      return getBuilder();
    }

    protected B dataType(final Class<D> dataType) {
      this.dataType = dataType;
      return getBuilder();
    }

    public M build() {

      final M metis = getMetis();

      metis.setMethod(method);
      metis.setUrl(url);

      Optional.ofNullable(queryParameters)
          .ifPresent(queryParameters ->
              queryParameters.forEach(metis::setQueryParameter));

      Optional.ofNullable(headers)
          .ifPresent(headers -> headers.forEach(metis::setHeader));

      metis.setDataType(dataType);

      return metis;
    }

    public MetisResult<D> send() throws TethysIOException {
      return build().send();
    }

    protected abstract M getMetis();
    protected abstract B getBuilder();

  }

}
