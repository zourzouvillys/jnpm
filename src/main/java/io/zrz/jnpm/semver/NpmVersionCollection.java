package io.zrz.jnpm.semver;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Joiner;

import lombok.EqualsAndHashCode;

/**
 * A collection of version specifications which together are evaluated in OR or
 * AND.
 * 
 * @author theo
 *
 */

@EqualsAndHashCode
public class NpmVersionCollection implements NpmVersionRange {

  private final List<NpmVersionRange> items;
  private final NpmBinaryOperator op;

  public NpmVersionCollection(NpmBinaryOperator op, List<NpmVersionRange> items) {
    this.op = Objects.requireNonNull(op);
    this.items = Objects.requireNonNull(items);
  }

  @Override
  public boolean satisfiedBy(NpmExactVersion version) {

    switch (op) {

      case And:
        for (NpmVersionRange spec : this.items) {
          if (!spec.satisfiedBy(version)) {
            return false;
          }
        }
        return true;

      case Or:
        for (NpmVersionRange spec : this.items) {
          if (spec.satisfiedBy(version)) {
            return true;
          }
        }
        return false;

      default:
        throw new IllegalArgumentException(op.toString());

    }

  }

  @Override
  public <R> R apply(NpmVersionRangeVisitor<R> visitor) {
    return visitor.visitCollection(this);
  }

  @Override
  public String toString() {
    return Joiner.on((op == NpmBinaryOperator.And) ? " " : " || ").join(items);
  }

}
