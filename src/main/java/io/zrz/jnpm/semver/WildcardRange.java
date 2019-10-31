package io.zrz.jnpm.semver;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

/**
 * A wildcard range, for example '1.x.x'.
 * 
 * @author theo
 *
 */

@Value.Immutable
@Value.Style(overshadowImplementation = true, deepImmutablesDetection = true)
public abstract class WildcardRange implements VersionRange {

  @Value.Parameter
  public abstract int major();

  @Value.Parameter
  public abstract int minor();

  @Value.Parameter
  public abstract int patch();

  // private WildcardRange(int major, int minor, int patch) {
  // Preconditions.checkArgument((major != -1) || ((minor == -1) && (patch == -1)));
  // Preconditions.checkArgument((minor != -1) || (patch == -1));
  // }

  @Override
  @JsonValue
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append(major() == -1 ? "X"
                            : major());
    sb.append(".");
    sb.append(minor() == -1 ? "X"
                            : minor());
    sb.append(".");
    sb.append(patch() == -1 ? "X"
                            : patch());

    return sb.toString();

  }

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitWildcardRange(this);
  }

  @Override
  public boolean satisfiedBy(ExactVersion version) {

    if (this.major() == -1) {
      return true;
    }
    else if (this.major() != version.major()) {
      return false;
    }

    if (this.minor() == -1) {
      return true;
    }
    else if (this.minor() != version.minor()) {
      return false;
    }

    if (this.patch() == -1) {
      return true;
    }
    else if (this.patch() != version.patch()) {
      return false;
    }

    return false;

  }

  public static WildcardRange fromParts() {
    return fromParts(-1, -1, -1);
  }

  public static WildcardRange fromParts(int major) {
    return fromParts(major, -1, -1);
  }

  public static WildcardRange fromParts(int major, int minor) {
    return fromParts(major, minor, -1);
  }

  public static WildcardRange fromParts(int major, int minor, int patch) {
    Preconditions.checkArgument((major != -1) || ((minor == -1) && (patch == -1)));
    Preconditions.checkArgument((minor != -1) || (patch == -1));
    return ImmutableWildcardRange.of(major, minor, patch);
  }

}
