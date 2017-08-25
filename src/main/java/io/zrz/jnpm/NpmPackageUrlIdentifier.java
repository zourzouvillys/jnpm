package io.zrz.jnpm;

import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.VersionRange;
import io.zrz.jnpm.semver.SemanticVersionVisitor;

public class NpmPackageUrlIdentifier implements VersionRange {

  private String value;

  public NpmPackageUrlIdentifier(String value) {
    this.value = value;
  }

  @Override
  public boolean satisfiedBy(ExactVersion version) {
    throw new IllegalArgumentException(value);
  }

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitNpmPackageUrlIdentifier(this);
  }

}
