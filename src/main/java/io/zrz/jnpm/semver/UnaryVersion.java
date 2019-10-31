package io.zrz.jnpm.semver;

import org.apache.commons.lang3.NotImplementedException;

/**
 * A unary version operator.
 *
 * @author theo
 *
 */

public class UnaryVersion implements VersionRange {

  private final VersionOperator op;
  private final ExactVersion version;

  public UnaryVersion(VersionOperator op, ExactVersion version) {
    this.op = op;
    this.version = version;
  }
  
  public VersionOperator op() {
    return this.op;
  }
  
  public ExactVersion exactVersion() {
    return this.version;
  }

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitUnaryExpressionVersion(this);
  }

  @Override
  public boolean satisfiedBy(ExactVersion other) {

    switch (this.op) {

      case Greater:
        return other.compareTo(this.version) > 0;

      case GreaterOrEqual:
        return other.compareTo(this.version) >= 0;

      case Less:
        return other.compareTo(this.version) < 0;

      case LessOrEqual:
        return other.compareTo(this.version) <= 0;

      case TildeRange:

        if (!VersionUtils.allowedMatch(this.version, other)) {
          return false;
        }

        if (other.compareTo(this.version) < 0) {
          return false;
        }

        final ExactVersion test =
          this.version
            .withIncrement((this.version.rightMostNonZeroPosition(VersionPart.Major) == VersionPart.Major)
                                                                                                           ? VersionPart.Major
                                                                                                           : VersionPart.Minor);

        return other.compareTo(test) < 0;

      default:

        throw new NotImplementedException(this.op.toString());

    }

  }

  @Override
  public String toString() {
    return String.format("%s%s", this.op, this.version);
  }

}
