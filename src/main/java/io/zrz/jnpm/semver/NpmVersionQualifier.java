package io.zrz.jnpm.semver;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Builder
@Wither
public class NpmVersionQualifier {

  private static final NpmVersionQualifier EMPTY = new NpmVersionQualifier(null, null);

  /**
   * The prerelease tag.
   */

  private final String pre;

  /**
   * The build tag.
   */

  private final String buildTag;

  public NpmVersionQualifier withoutPrerelease() {
    return this.withPre(null);
  }

  public static NpmVersionQualifier emptyQualifier() {
    return EMPTY;
  }

  @Override

  public String toString() {

    StringBuilder sb = new StringBuilder();

    if (this.pre != null) {
      sb.append('-').append(pre);
    }
    if (this.buildTag != null) {
      sb.append('+').append(buildTag);
    }

    return sb.toString();

  }

  public boolean isPrerelease() {
    return this.pre != null;
  }

}
