package io.zrz.jnpm.semver;

import org.apache.commons.lang3.NotImplementedException;

import io.zrz.jnpm.NpmUtils;
import io.zrz.jnpm.NpmVersionOperator;
import lombok.Getter;

/**
 * A unary version operator.
 * 
 * @author theo
 *
 */

public class NpmUnaryVersion implements NpmVersionRange {

  @Getter
  private NpmVersionOperator op;

  @Getter
  private NpmExactVersion version;

  public NpmUnaryVersion(NpmVersionOperator op, NpmExactVersion version) {
    this.op = op;
    this.version = version;
  }

  @Override
  public <R> R apply(NpmVersionRangeVisitor<R> visitor) {
    return visitor.visitUnaryExpressionVersion(this);
  }

  @Override
  public boolean satisfiedBy(NpmExactVersion other) {

    switch (op) {

      case Greater:
        return other.compareTo(this.version) > 0;

      case GreaterOrEqual:
        return other.compareTo(this.version) >= 0;

      case Less:
        return other.compareTo(this.version) < 0;

      case LessOrEqual:
        return other.compareTo(this.version) <= 0;

      case TildeRange:

        if (!NpmUtils.allowedMatch(this.version, other)) {
          return false;
        }

        if (other.compareTo(this.version) < 0) {
          return false;
        }

        NpmExactVersion test = this.version
            .withIncrement((this.version.rightMostNonZeroPosition(NpmVersionPart.Major) == NpmVersionPart.Major)
                ? NpmVersionPart.Major : NpmVersionPart.Minor);

        return other.compareTo(test) < 0;

      default:

        throw new NotImplementedException(op.toString());

    }

  }

  @Override
  public String toString() {
    return String.format("%s%s", op, version);
  }

}
