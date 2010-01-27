package elemental.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class Io {
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  public static void write(InputStream from, OutputStream to, int bufferSize) throws IOException {
    int n;
    final byte[] buffer = new byte[bufferSize];
    while ((n = from.read(buffer)) >= 0)
      to.write(buffer, 0, n);
  }
  
  public static void read(Reader reader, int bufferSize, StringBuffer result)
      throws IOException {
    final char[] buffer = new char[bufferSize];
    int n;
    while ((n = reader.read(buffer)) >= 0)
      result.append(buffer, 0, n);
  }

  public static String read(Reader reader) throws IOException {
    return read(reader, DEFAULT_BUFFER_SIZE);
  }

  public static String read(Reader reader, int bufferSize) throws IOException {
    final StringBuffer result = new StringBuffer();
    read(reader, bufferSize, result);
    return result.toString();
  }

  public static String read(InputStream stream, int bufferSize, String encoding)
      throws IOException {
    return read(new InputStreamReader(stream, encoding), bufferSize);
  }

  public static String read(InputStream stream) throws IOException {
    return read(new InputStreamReader(stream, "UTF-8"), DEFAULT_BUFFER_SIZE);
  }

  private Io() {
  }
}
