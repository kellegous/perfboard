package elemental.base;

public class StringUtil {
  public static String ljust(String src, int width) {
    return ljust(new StringBuffer(), src, width, ' ').toString();
  }

  public static String ljust(String src, int width, char chr) {
    return ljust(new StringBuffer(), src, width, chr).toString();
  }

  public static StringBuffer ljust(StringBuffer buffer, String src, int width) {
    return ljust(buffer, src, width, ' ');
  }

  public static StringBuffer ljust(StringBuffer buffer, String src, int width,
      char chr) {
    int length = src.length();
    buffer.append(src);
    while (length++ < width) {
      buffer.append(chr);
    }
    return buffer;
  }

  public static String rjust(String src, int width) {
    return rjust(new StringBuffer(), src, width, ' ').toString();
  }

  public static String rjust(String src, int width, char chr) {
    return rjust(new StringBuffer(), src, width, chr).toString();
  }

  public static StringBuffer rjust(StringBuffer buffer, String src, int width) {
    return rjust(buffer, src, width, ' ');
  }

  public static StringBuffer rjust(StringBuffer buffer, String src, int width,
      char chr) {
    int length = src.length();
    while (length++ < width) {
      buffer.append(chr);
    }
    buffer.append(src);
    return buffer;
  }

  private static char[] HEX_CHARS = new char[] {
      '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String getHexString(byte[] bytes) {
    final int len = bytes.length;
    final char[] chars = new char[len * 2];
    int j = 0;
    for (int i = 0; i < len; ++i) {
      int b = bytes[i] & 0xff;
      chars[j++] = HEX_CHARS[b >>> 4];
      chars[j++] = HEX_CHARS[b & 0xf];
    }
    return new String(chars);
  }

  public static boolean isNumeric(String str) {
    if (str.length() == 0) {
      return false;
    }

    for (int i = 0, n = str.length(); i < n; ++i) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private StringUtil() {
  }
}
