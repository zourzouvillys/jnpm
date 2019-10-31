package io.zrz.jnpm.semver;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Joiner;

/**
 * A collection of version specifications which together are evaluated in OR or AND.
 * 
 * @author theo
 *
 */

public class VersionCollection implements VersionRange {

  private final List<VersionRange> items;
  private final BinaryOperator op;

  public VersionCollection(BinaryOperator op, List<VersionRange> items) {
    this.op = Objects.requireNonNull(op);
    this.items = Objects.requireNonNull(items);
  }

  @Override
  public boolean satisfiedBy(ExactVersion version) {

    switch (op) {

      case And:
        for (VersionRange spec : this.items) {
          if (!spec.satisfiedBy(version)) {
            return false;
          }
        }
        return true;

      case Or:
        for (VersionRange spec : this.items) {
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
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitCollection(this);
  }

  @Override
  public String toString() {
    return Joiner.on((op == BinaryOperator.And) ? " "
                                                : " || ")
      .join(items);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof VersionCollection) {
      return new EqualsBuilder().append(items, ((VersionCollection) other).items)
        .append(op, ((VersionCollection) other).op)
        .build();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(items).append(op).build();
  }

}
