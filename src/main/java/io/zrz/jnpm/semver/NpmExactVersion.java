package io.zrz.jnpm.semver;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CharMatcher;

import io.zrz.jnpm.NpmVersion;
import io.zrz.jnpm.NpmVersionParser;
import lombok.EqualsAndHashCode;

/**
 * A representation of a concrete semantic version number.
 * 
 * @author theo
 *
 */

@EqualsAndHashCode
public class NpmExactVersion implements NpmVersionRange, Comparable<NpmExactVersion>, NpmVersion {

  private final int major, minor, patch;
  private NpmVersionQualifier qualifier;

  public NpmExactVersion(int major, int minor, int patch) {
    this(major, minor, patch, NpmVersionQualifier.emptyQualifier());
  }

  public NpmExactVersion(int major, int minor, int patch, NpmVersionQualifier qualifier) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.qualifier = Objects.requireNonNull(qualifier);
  }

  public NpmVersionQualifier qualifier() {
    return this.qualifier;
  }

  public NpmExactVersion withoutPrerelease() {
    return new NpmExactVersion(major, minor, patch, qualifier.withoutPrerelease());
  }

  public NpmExactVersion withIncrement(NpmVersionPart part) {
    switch (part) {
      case Major:
        return new NpmExactVersion(major + 1, 0, 0);
      case Minor:
        return new NpmExactVersion(major, minor + 1, 0);
      case Patch:
        return new NpmExactVersion(major, minor, patch + 1);
    }
    throw new IllegalArgumentException(part.toString());
  }

  /**
   * 
   * @param part
   * @param pre
   * @return
   */

  public NpmExactVersion withPrerelease(NpmVersionPart part, String pre) {
    switch (part) {
      case Major:
        return new NpmExactVersion(major + 1, 0, 0, pre(pre, 0));
      case Minor:
        return new NpmExactVersion(major, minor + 1, 0, pre(pre, 0));
      case Patch:
        return new NpmExactVersion(major, minor, patch + 1, pre(pre, 0));
    }
    throw new IllegalArgumentException(part.toString());
  }

  private NpmVersionQualifier pre(String pre, int ver) {
    if (pre == null)
      return qualifier.withPre(Integer.toString(ver));
    return qualifier.withPre(String.format("%s.%d", pre, ver));
  }

  /**
   * calculates the new tag.
   */

  private String pre(String tag) {

    String pfx = String.format("%s.", tag);

    // is there an existing tag?

    if (this.qualifier.getPre() == null || !StringUtils.startsWith(this.qualifier.getPre(), pfx)) {
      if (tag == null) {
        return "0";
      }
      return String.format("%s.%d", tag, 0);
    }

    String current = this.qualifier.getPre().substring(pfx.length());

    if (!current.isEmpty() && CharMatcher.inRange('0', '9').matchesAllOf(current)) {
      return pfx + (Integer.parseInt(current) + 1);
    }

    // reset it,.

    return pfx + '0';

  }

  public NpmExactVersion withPrerelease(String pre) {
    if (this.qualifier.getPre() != null) {
      return new NpmExactVersion(major, minor, patch, this.qualifier.withPre(pre(pre)));
    }
    return withPrerelease(NpmVersionPart.Patch, pre);
  }

  public NpmExactVersion withPrerelease() {
    return withPrerelease(null);
  }

  public NpmExactVersion withBuild(String build) {
    return this;
  }

  //

  @Override
  public <R> R apply(NpmVersionRangeVisitor<R> visitor) {
    return visitor.visitExactVersion(this);
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

    sb.append(this.qualifier.toString());

    return sb.toString();

  }

  public int major() {
    return major;
  }

  public int minor() {
    return minor;
  }

  public int patch() {
    return patch;
  }

  /**
   * returns the right most element which has a positive value.
   * 
   * fields which have a wildcard value do not count.
   * 
   * @param defaultValue
   * @return
   */

  public NpmVersionPart leftMostNonZeroPosition() {

    if (this.major() > 0) {
      return NpmVersionPart.Major;
    }

    if (this.minor() > 0) {
      return NpmVersionPart.Minor;
    }

    if (this.patch() > 0) {
      return NpmVersionPart.Patch;
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

  public NpmVersionPart rightMostNonZeroPosition(NpmVersionPart defaultValue) {

    if (this.patch() > 0) {
      return NpmVersionPart.Patch;
    }

    if (this.minor() > 0) {
      return NpmVersionPart.Minor;
    }

    if (this.major() > 0) {
      return NpmVersionPart.Major;
    }

    return defaultValue;

  }

  @Override
  public boolean satisfiedBy(NpmExactVersion version) {
    return this.equals(version);
  }

  public static NpmExactVersion fromString(String string) {
    return NpmVersionParser.getDefaultInstance().parsePartial(string);
  }

  public int compareTo(NpmExactVersion other) {

    if (this.major != -1 && other.major != -1) {
      if (this.major != other.major)
        return Integer.compare(this.major, other.major);
    }

    if (this.minor != -1 && other.minor != -1) {
      if (this.minor != other.minor)
        return Integer.compare(this.minor, other.minor);
    }

    if (this.patch != -1 && other.patch != -1) {
      if (this.patch != other.patch)
        return Integer.compare(this.patch, other.patch);
    }

    if (!StringUtils.equals(this.qualifier.getPre(), other.qualifier.getPre())) {
      if (this.qualifier.getPre() != null && other.qualifier.getPre() == null)
        return -1;
      if (this.qualifier.getPre() == null && other.qualifier.getPre() != null)
        return 1;
      return this.qualifier.getPre().compareTo(other.qualifier.getPre());
    }

    return 0;
  }

  public boolean isPrerelease() {
    return this.qualifier.isPrerelease();
  }

}
