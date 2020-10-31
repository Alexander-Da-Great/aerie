package gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.resources.real;

/**
 * A description of a time-dependent behavior for real-valued resources that may vary continuously.
 *
 * <p>
 *   This class currently only supports constant and linear dynamics, but we hope to add more in the future.
 * </p>
 */
public abstract class RealDynamics {
  private RealDynamics() {}

  public abstract <Result> Result match(final Visitor<Result> visitor);

  public interface Visitor<Result> {
    // λt. value
    Result constant(double value);

    // λt. intercept + t * slope
    Result linear(double intercept, double slope);

    // In the future: polynomials, maybe differential systems.
  }

  public static RealDynamics constant(final double value) {
    return new RealDynamics() {
      @Override
      public <Result> Result match(final Visitor<Result> visitor) {
        return visitor.constant(value);
      }
    };
  }

  public static RealDynamics linear(final double intercept, final double slope) {
    return new RealDynamics() {
      @Override
      public <Result> Result match(final Visitor<Result> visitor) {
        return visitor.linear(intercept, slope);
      }
    };
  }

  @Override
  public String toString() {
    return this.match(new Visitor<>() {
      @Override
      public String constant(final double value) {
        return "λt. " + value;
      }

      @Override
      public String linear(final double intercept, final double slope) {
        return "λt. " + intercept + " + t * " + slope;
      }
    });
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof RealDynamics)) return false;
    final var other = (RealDynamics) o;

    // The reader may ask, why not implement at least one level of this dispatch
    //   as equals() methods on each individual subclass?
    // We want to make sure that, whenever we add new dynamic behaviors,
    //   all combinations are accounted for.
    // Since Object already has a default equals method,
    //   relying on dynamic dispatch would make it easy to miss a case.
    // It may be a little ugly in Java,
    //   but a two-level match makes it impossible to miss a case.
    // (We can always add a `DefaultVisitor` to reduce the combinatorial explosion.)
    return this.match(new Visitor<>() {
      @Override
      public Boolean constant(final double value1) {
        return other.match(new Visitor<>() {
          @Override
          public Boolean constant(final double value2) {
            return (value1 == value2);
          }

          @Override
          public Boolean linear(final double intercept2, final double slope2) {
            return false;
          }
        });
      }

      @Override
      public Boolean linear(final double intercept1, final double slope1) {
        return other.match(new Visitor<>() {
          @Override
          public Boolean constant(final double value2) {
            return false;
          }

          @Override
          public Boolean linear(final double intercept2, final double slope2) {
            return (intercept1 == intercept2) && (slope1 == slope2);
          }
        });
      }
    });
  }
}
