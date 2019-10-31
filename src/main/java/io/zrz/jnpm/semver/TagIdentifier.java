package io.zrz.jnpm.semver;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TagIdentifier implements SemanticVersion, VersionRange {

  public static final TagIdentifier LATEST = new TagIdentifier("latest");
  public static final TagIdentifier NEXT = new TagIdentifier("next");

  private final String tag;

  public TagIdentifier(String value) {
    this.tag = value;
  }

  public String tag() {
    return this.tag;
  }

  @Override
  public String toString() {
    return this.tag;
  }

  /**
   * if this happens, it means the API consumer did not resolve the version against the dist.
   */

  @Override
  public boolean satisfiedBy(ExactVersion version) {
    throw new IllegalArgumentException("Unresolved version");
  }

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitTag(this);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof TagIdentifier) {
      return new EqualsBuilder().append(tag, ((TagIdentifier) other).tag)
        .build();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(tag).build();
  }

}
