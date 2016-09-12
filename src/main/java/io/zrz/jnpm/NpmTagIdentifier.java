package io.zrz.jnpm;

import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;
import io.zrz.jnpm.semver.NpmVersionRangeVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class NpmTagIdentifier implements NpmVersion, NpmVersionRange {

  public static final NpmTagIdentifier LATEST = new NpmTagIdentifier("latest");
  public static final NpmTagIdentifier NEXT = new NpmTagIdentifier("next");

  @Getter
  private String tag;

  public NpmTagIdentifier(String value) {
    this.tag = value;
  }

  @Override
  public String toString() {
    return this.tag;
  }

  /**
   * if this happens, it means the API consumer did not resolve the version
   * against the dist.
   */

  @Override
  public boolean satisfiedBy(NpmExactVersion version) {
    throw new IllegalArgumentException("Unresolved version");
  }

  @Override
  public <R> R apply(NpmVersionRangeVisitor<R> visitor) {
    return visitor.visitTag(this);
  }

}
