package elemental.base;

public class Duration {
  private static final double NANOS_PER_SECOND = 1000000000.0;
  private static final double MILLIS_PER_SECOND = 1000000.0;

  private final long startedAt = System.nanoTime();

  private long elapsed() {
    return System.nanoTime() - startedAt;
  }

  public double elapsedTimeInMilliseconds() {
    return elapsed() / MILLIS_PER_SECOND;
  }

  public double elapsedTimeInSeconds() {
    return elapsed() / NANOS_PER_SECOND;
  }
}
