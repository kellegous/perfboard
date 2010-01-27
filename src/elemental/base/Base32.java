package elemental.base;

public class Base32 {
  public static class DecodeException extends Exception {
    private static final long serialVersionUID = 5680397078363072399L;
  }
  
  public static byte[] decode(String data) throws DecodeException {
    final int len = data.length();
    final byte[] result = new byte[len * 5 / 8];

    int buffer = 0;
    int bufferSize = 0;
    int j = 0;

    for (int i = 0; i < len; ++i) {
      final char c = data.charAt(i);
      buffer <<= 5;
      buffer |= toIndexFromChar(c);
      bufferSize += 5;
      if (bufferSize >= 8) {
        bufferSize -= 8;
        result[j++] = (byte) (buffer >>> bufferSize);
      }
    }

    if ((buffer & (1 << bufferSize) - 1) != 0) {
      throw new DecodeException();
    }

    return result;
  }

  public static String encode(byte[] data) {
    final int len = data.length;

    int resultLen = len * 8;
    resultLen = (resultLen % 5 == 0) ? resultLen / 5 : resultLen / 5 + 1;
    final char[] result = new char[resultLen];

    int buffer = 0;
    int bufferSize = 0;

    int j = 0;
    for (int i = 0; i < len; ++i) {
      final byte b = data[i];
      buffer <<= 8;
      buffer |= b & 0xff;
      bufferSize += 8;
      while (bufferSize >= 5) {
        bufferSize -= 5;
        result[j++] = toCharFromIndex((buffer >>> bufferSize) & 0x1f);
      }
    }

    if (bufferSize != 0) {
      buffer <<= 5 - bufferSize;
      result[j++] = toCharFromIndex(buffer & 0x1f);
    }
    return new String(result);
  }

  static private char toCharFromIndex(int v) {
    return (char) (v < 26 ? v + 'a' : v - 26 + '2');
  }

  private static int toIndexFromChar(char c) throws DecodeException {
    if (c >= 'a' && c <= 'z') {
      return c - 'a';
    }

    if (c >= '2' && c <= '7') {
      return 26 + (c - '2');
    }

    throw new DecodeException();
  }

  private Base32() {
  }
}
