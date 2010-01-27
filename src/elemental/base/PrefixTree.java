package elemental.base;

/**
 * @author Kelly Norton <kel@kellegous.com>
 * 
 *         TODO(knorton): This API for this is a bit bonked. At one point I
 *         needed to be able to get the prefix that resolved the value, but I
 *         don't need that any more. So, there should be different methods now:
 *         one that just returns you the T value, the other that gives you an
 *         Entry.
 * 
 * @param <T>
 */
public class PrefixTree<T> {

  /**
   * 
   * @param <A>
   */
  public static class Entry<A> {
    private final String prefix;
    private final Node<A> node;

    private Entry(String prefix, Node<A> node) {
      this.prefix = prefix;
      this.node = node;
    }

    public String getPrefix() {
      return prefix;
    }

    public A getValue() {
      return node.data;
    }
  }

  /**
   * 
   * @param <A>
   */
  private static class Node<A> {
    final char splitChar;

    A data;

    Node<A> lo, ea, hi;

    Node(char splitChar) {
      this.splitChar = splitChar;
    }
  }

  private Node<T> root = null;

  public void put(String prefix, T obj) {
    placeNode(prefix).data = obj;
  }

  public Entry<T> getEntry(String key) {
    return findEntry(key, root, null, 0, key.length());
  }

  public T get(String key) {
    // TODO(knorton): Make this efficient.
    final Entry<T> entry = getEntry(key);
    return entry != null ? entry.getValue() : null;
  }

  private Entry<T> findEntry(String key, Node<T> cNode, Entry<T> sEntry,
      int cur, int len) {
    if (cNode == null || cur == len) {
      return sEntry;
    } else {
      final int cp = key.charAt(cur) - cNode.splitChar;
      if (cp == 0) {
        final int nex = cur + 1;
        return findEntry(key, cNode.ea, (cNode.data == null) ? sEntry
            : new Entry<T>(key.substring(0, nex), cNode), nex, len);
      } else {
        return findEntry(key, (cp < 0) ? cNode.lo : cNode.hi, sEntry, cur, len);
      }
    }
  }

  private Node<T> placeNode(String key) {
    if (root == null) {
      root = new Node<T>(key.charAt(0));
    }

    Node<T> nd = root;
    int ix = 0, ln = key.length();

    while (true) {
      int cp = key.charAt(ix) - nd.splitChar;
      if (cp == 0) {
        ix++;
        if (ix == ln) {
          return nd;
        }
        if (nd.ea == null) {
          nd.ea = new Node<T>(key.charAt(ix));
        }
        nd = nd.ea;
      } else if (cp < 0) {
        if (nd.lo == null) {
          nd.lo = new Node<T>(key.charAt(ix));
        }
        nd = nd.lo;
      } else {
        if (nd.hi == null) {
          nd.hi = new Node<T>(key.charAt(ix));
        }
        nd = nd.hi;
      }
    }
  }
}
