package elemental.base;

public class CommandLine {
  public abstract static class Flag<T> {
    private final String name;
    private final String description;
    private final boolean required;
    private T value;

    public Flag(String name, String description, T value, boolean required) {
      this.name = name;
      this.description = description;
      this.value = value;
      this.required = required;
    }

    public Flag(String name, String description, T value) {
      this(name, description, value, false);
    }

    public T get() {
      return value;
    }

    public String getDescription() {
      return description;
    }

    public String getName() {
      return name;
    }

    public abstract boolean parse(Pair<String, String> arg);

    protected void set(T value) {
      this.value = value;
    }

    public boolean isRequired() {
      return required;
    }
  }

  public static class Result {
    private final boolean didParse;

    private final String[] remainingArgs;

    private static Result fail() {
      return new Result();
    }

    private static Result succeed(String[] remaining) {
      return new Result(remaining);
    }

    private Result() {
      this.didParse = false;
      this.remainingArgs = null;
    }

    private Result(String[] remainingArgs) {
      this.didParse = true;
      this.remainingArgs = remainingArgs;
    }

    public boolean didParse() {
      return didParse;
    }

    public String[] getRemainingArgs() {
      return remainingArgs;
    }
  }

  public static class StringFlag extends Flag<String> {
    public StringFlag(String name, String description, String defaultValue) {
      super(name, description, defaultValue);
    }

    @Override
    public boolean parse(Pair<String, String> arg) {
      if (!getName().equals(arg.getA())) {
        return false;
      }
      set(arg.getB());
      return true;
    }
  }

  public static class IntegerFlag extends Flag<Integer> {
    public IntegerFlag(String name, String description, int defaultValue) {
      super(name, description, Integer.valueOf(defaultValue));
    }

    @Override
    public boolean parse(Pair<String, String> arg) {
      if (!getName().equals(arg.getA()))
        return false;
      try {
        set(Integer.valueOf(Integer.parseInt(arg.getB())));
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  public static class BooleanFlag extends Flag<Boolean> {
    public BooleanFlag(String name, String description) {
      super(name, description, Boolean.FALSE);
    }

    @Override
    public boolean parse(Pair<String, String> arg) {
      if (!getName().equals(arg.getA())) {
        return false;
      }
      set(Boolean.TRUE);
      return true;
    }
  }

  private static Pair<String, String> parseArgPair(String arg) {
    assert arg.startsWith("--");
    final int ix = arg.indexOf('=');
    return (ix < 0) ? new Pair<String, String>(arg.substring(2), null)
        : new Pair<String, String>(arg.substring(2, ix), arg.substring(ix + 1));
  }

  private static String[] getSubArray(String[] src, int startAt) {
    final String[] result = new String[src.length - startAt];
    for (int i = 0, n = result.length; i < n; ++i) {
      result[i] = src[i + startAt];
    }
    return result;
  }

  private static boolean isFlag(String arg) {
    return arg.startsWith("--");
  }

  private static boolean parse(String arg, Flag<?>[] flags) {
    final Pair<String, String> pair = parseArgPair(arg);
    if (pair != null) {
      for (Flag<?> f : flags) {
        if (f.parse(pair))
          return true;
      }
    }
    return false;
  }

  public static Result parse(String[] args, Flag<?>... flags) {
    for (int i = 0, n = args.length; i < n; ++i) {
      // There are no more flags.
      if (!isFlag(args[i])) {
        return Result.succeed(getSubArray(args, i));
      }

      // Try to parse the arg with any of our set of flags.
      if (!parse(args[i], flags)) {
        return Result.fail();
      }
    }
    return Result.succeed(new String[0]);
  }

  private CommandLine() {
  }
}
