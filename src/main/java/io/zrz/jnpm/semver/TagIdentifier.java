package io.zrz.jnpm.semver;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class TagIdentifier implements SemanticVersion, VersionRange {

  public static final TagIdentifier LATEST = new TagIdentifier("latest");
  public static final TagIdentifier NEXT = new TagIdentifier("next");

  @Getter
  private String tag;

  public TagIdentifier(String value) {
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
  public boolean satisfiedBy(ExactVersion version) {
    throw new IllegalArgumentException("Unresolved version");
  }

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitTag(this);
  }

}
