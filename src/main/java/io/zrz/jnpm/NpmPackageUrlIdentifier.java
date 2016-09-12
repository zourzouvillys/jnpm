package io.zrz.jnpm;

import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;
import io.zrz.jnpm.semver.NpmVersionRangeVisitor;

public class NpmPackageUrlIdentifier implements NpmVersionRange {

  private String value;

  public NpmPackageUrlIdentifier(String value) {
    this.value = value;
  }

  @Override
  public boolean satisfiedBy(NpmExactVersion version) {
    throw new IllegalArgumentException(value);
  }

  @Override
  public <R> R apply(NpmVersionRangeVisitor<R> visitor) {
    return visitor.visitNpmPackageUrlIdentifier(this);
  }

}
