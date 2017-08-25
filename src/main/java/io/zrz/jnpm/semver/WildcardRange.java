package io.zrz.jnpm.semver;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;

/**
 * A wildcard range, for example '1.x.x'.
 * 
 * @author theo
 *
 */

@EqualsAndHashCode
public class WildcardRange implements VersionRange {

  private final int major, minor, patch;

  public WildcardRange(int major, int minor, int patch) {

    this.major = major;
    this.minor = minor;
    this.patch = patch;

    Preconditions.checkArgument(major != -1 || (minor == -1 && patch == -1));
    Preconditions.checkArgument(minor != -1 || (patch == -1));

  }

  @Override
  @JsonValue
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append(major == -1 ? "X" : major);
    sb.append(".");
    sb.append(minor == -1 ? "X" : minor);
    sb.append(".");
    sb.append(patch == -1 ? "X" : patch);

    return sb.toString();

  }

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitWildcardRange(this);
  }

  @Override
  public boolean satisfiedBy(ExactVersion version) {

    if (this.major == -1) {
      return true;
    } else if (this.major != version.major()) {
      return false;
    }

    if (this.minor == -1) {
      return true;
    } else if (this.minor != version.minor()) {
      return false;
    }

    if (this.patch == -1) {
      return true;
    } else if (this.patch != version.patch()) {
      return false;
    }

    return false;

  }

  public static WildcardRange fromParts() {
    return new WildcardRange(-1, -1, -1);
  }

  public static WildcardRange fromParts(int major) {
    return new WildcardRange(major, -1, -1);
  }

  public static WildcardRange fromParts(int major, int minor) {
    return new WildcardRange(major, minor, -1);
  }

  public static WildcardRange fromParts(int major, int minor, int patch) {
    return new WildcardRange(major, minor, patch);
  }

}
