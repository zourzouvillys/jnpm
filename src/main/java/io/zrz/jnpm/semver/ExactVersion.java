package io.zrz.jnpm.semver;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CharMatcher;

import lombok.EqualsAndHashCode;

/**
 * A concrete semantic version number.
 *
 * @author theo
 *
 */

@EqualsAndHashCode
public class ExactVersion implements VersionRange, Comparable<ExactVersion>, SemanticVersion {

  private final int major, minor, patch;
  private final VersionQualifier qualifier;

  public ExactVersion(int major, int minor, int patch) {
    this(major, minor, patch, VersionQualifier.emptyQualifier());
  }

  public ExactVersion(int major, int minor, int patch, VersionQualifier qualifier) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.qualifier = Objects.requireNonNull(qualifier);
  }

  public VersionQualifier qualifier() {
    return this.qualifier;
  }

  public ExactVersion withoutPrerelease() {
    return new ExactVersion(this.major, this.minor, this.patch, this.qualifier.withoutPrerelease());
  }

  public ExactVersion withIncrement(VersionPart part) {
    switch (part) {
      case Major:
        return new ExactVersion(this.major + 1, 0, 0);
      case Minor:
        return new ExactVersion(this.major, this.minor + 1, 0);
      case Patch:
        return new ExactVersion(this.major, this.minor, this.patch + 1);
    }
    throw new IllegalArgumentException(part.toString());
  }

  /**
   *
   * @param part
   * @param pre
   * @return
   */

  public ExactVersion withPrerelease(VersionPart part, String pre) {
    switch (part) {
      case Major:
        return new ExactVersion(this.major + 1, 0, 0, this.pre(pre, 0));
      case Minor:
        return new ExactVersion(this.major, this.minor + 1, 0, this.pre(pre, 0));
      case Patch:
        return new ExactVersion(this.major, this.minor, this.patch + 1, this.pre(pre, 0));
    }
    throw new IllegalArgumentException(part.toString());
  }

  private VersionQualifier pre(String pre, int ver) {
    if (pre == null) {
      return this.qualifier.withPre(Integer.toString(ver));
    }
    return this.qualifier.withPre(String.format("%s.%d", pre, ver));
  }

  /**
   * calculates the new tag.
   */

  private String pre(String tag) {

    final String pfx = String.format("%s.", tag);

    // is there an existing tag?

    if (this.qualifier.getPre() == null || !StringUtils.startsWith(this.qualifier.getPre(), pfx)) {
      if (tag == null) {
        return "0";
      }
      return String.format("%s.%d", tag, 0);
    }

    final String current = this.qualifier.getPre().substring(pfx.length());

    if (!current.isEmpty() && CharMatcher.inRange('0', '9').matchesAllOf(current)) {
      return pfx + (Integer.parseInt(current) + 1);
    }

    // reset it,.

    return pfx + '0';

  }

  public ExactVersion withPrerelease(String pre) {
    if (this.qualifier.getPre() != null) {
      return new ExactVersion(this.major, this.minor, this.patch, this.qualifier.withPre(this.pre(pre)));
    }
    return this.withPrerelease(VersionPart.Patch, pre);
  }

  public ExactVersion withPrerelease() {
    return this.withPrerelease(null);
  }

  public ExactVersion withBuild(String build) {
    return this;
  }

  //

  @Override
  public <R> R apply(SemanticVersionVisitor<R> visitor) {
    return visitor.visitExactVersion(this);
  }

  @Override
  @JsonValue
  public String toString() {

    final StringBuilder sb = new StringBuilder();

    sb.append(this.major == -1 ? "X" : this.major);
    sb.append(".");
    sb.append(this.minor == -1 ? "X" : this.minor);
    sb.append(".");
    sb.append(this.patch == -1 ? "X" : this.patch);

    sb.append(this.qualifier.toString());

    return sb.toString();

  }

  public int major() {
    return this.major;
  }

  public int minor() {
    return this.minor;
  }

  public int patch() {
    return this.patch;
  }

  /**
   * returns the right most element which has a positive value.
   *
   * fields which have a wildcard value do not count.
   *
   * @param defaultValue
   * @return
   */

  public VersionPart leftMostNonZeroPosition() {

    if (this.major() > 0) {
      return VersionPart.Major;
    }

    if (this.minor() > 0) {
      return VersionPart.Minor;
    }

    if (this.patch() > 0) {
      return VersionPart.Patch;
    }

    return null;

  }

  /**
   * returns the right most element which has a positive value.
   *
   * fields which have a wildcard value do not count.
   *
   * @param defaultValue
   * @return
   */

  public VersionPart rightMostNonZeroPosition(VersionPart defaultValue) {

    if (this.patch() > 0) {
      return VersionPart.Patch;
    }

    if (this.minor() > 0) {
      return VersionPart.Minor;
    }

    if (this.major() > 0) {
      return VersionPart.Major;
    }

    return defaultValue;

  }

  @Override
  public boolean satisfiedBy(ExactVersion version) {
    return this.equals(version);
  }

  public static ExactVersion fromString(String string) {
    return VersionParser.parseExact(string);
  }

  @Override
  public int compareTo(ExactVersion other) {

    if (this.major != -1 && other.major != -1) {
      if (this.major != other.major) {
        return Integer.compare(this.major, other.major);
      }
    }

    if (this.minor != -1 && other.minor != -1) {
      if (this.minor != other.minor) {
        return Integer.compare(this.minor, other.minor);
      }
    }

    if (this.patch != -1 && other.patch != -1) {
      if (this.patch != other.patch) {
        return Integer.compare(this.patch, other.patch);
      }
    }

    if (!StringUtils.equals(this.qualifier.getPre(), other.qualifier.getPre())) {
      if (this.qualifier.getPre() != null && other.qualifier.getPre() == null) {
        return -1;
      }
      if (this.qualifier.getPre() == null && other.qualifier.getPre() != null) {
        return 1;
      }
      return this.qualifier.getPre().compareTo(other.qualifier.getPre());
    }

    return 0;
  }

  public boolean isPrerelease() {
    return this.qualifier.isPrerelease();
  }

}
