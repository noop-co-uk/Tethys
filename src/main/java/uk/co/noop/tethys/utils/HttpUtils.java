package uk.co.noop.tethys.utils;

import uk.co.noop.themis.Themis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpUtils {

  private static final int MAX_SUCCESS_RESPONSE_CODE = 299;

  private static final Logger LOGGER =
      Logger.getLogger(HttpUtils.class.getSimpleName());

  public static void write(
      final HttpURLConnection httpURLConnection,
      final String outgoingData) throws IOException {

    Themis.validate("httpURLConnection", httpURLConnection)
        .againstNullObjects();

    Themis.validate("outgoingData", outgoingData)
        .againstBlankStrings();

    OutputStream outputStream = null;

    try {

      httpURLConnection.setDoOutput(true);
      outputStream = httpURLConnection.getOutputStream();

      final byte[] bytes = outgoingData.getBytes(StandardCharsets.UTF_8);

      outputStream.write(bytes, 0, bytes.length);

    } finally {

      if (Objects.nonNull(outputStream)) {
        try {
          outputStream.close();
        } catch (final IOException e) {

          LOGGER.log(
              Level.WARNING,
              "IOException caught while attempting to close 'outputStream'",
              e);
        }
      }
    }
  }

  public static String readAllLines(
      final HttpURLConnection httpURLConnection,
      final int responseCode) throws IOException {

    Themis.validate("httpURLConnection", httpURLConnection)
        .againstNullObjects();

    BufferedReader bufferedReader = null;

    final StringBuilder stringBuilder;

    try {

      bufferedReader =
          new BufferedReader(
              getInputStreamReader(httpURLConnection, responseCode));

      stringBuilder = new StringBuilder();

      boolean done = false;

      do {

        final String line = bufferedReader.readLine();

        if (Objects.nonNull(line)) {
          stringBuilder.append(line);
        } else {
          done = true;
        }

      } while(!done);

    } finally {

      if (Objects.nonNull(bufferedReader)) {
        try {
          bufferedReader.close();
        } catch (final IOException e) {

          LOGGER.log(
              Level.WARNING,
              "IOException caught while attempting to close 'bufferedReader'",
              e);
        }
      }
    }

    return stringBuilder.toString();
  }

  private static InputStreamReader getInputStreamReader(
      final HttpURLConnection httpURLConnection,
      final int responseCode) throws IOException {

    final InputStream inputStream;

    if (responseCode > MAX_SUCCESS_RESPONSE_CODE) {
      inputStream = httpURLConnection.getErrorStream();
    } else {
      inputStream = httpURLConnection.getInputStream();
    }

    return new InputStreamReader(inputStream);
  }

}
