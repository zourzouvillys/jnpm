package io.zrz.jnpm.semver;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Builder
@Wither
public class VersionQualifier implements VersionSpec {

  private static final VersionQualifier EMPTY = new VersionQualifier(null, null);

  /**
   * The prerelease tag.
   */

  private final String pre;

  /**
   * The build tag.
   */

  private final String buildTag;

  public VersionQualifier withoutPrerelease() {
    return this.withPre(null);
  }

  public static VersionQualifier emptyQualifier() {
    return EMPTY;
  }

  @Override

  public String toString() {

    final StringBuilder sb = new StringBuilder();

    if (this.pre != null) {
      sb.append('-').append(this.pre);
    }
    if (this.buildTag != null) {
      sb.append('+').append(this.buildTag);
    }

    return sb.toString();

  }

  public boolean isPrerelease() {
    return this.pre != null;
  }

}
