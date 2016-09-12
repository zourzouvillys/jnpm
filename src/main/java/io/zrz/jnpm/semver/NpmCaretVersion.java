package io.zrz.jnpm.semver;

import io.zrz.jnpm.NpmVersionParser;

/**
 * From <https://docs.npmjs.com/misc/semver#caret-ranges-123-025-004>:
 * 
 * Allows changes that do not modify the left-most non-zero digit in the [major,
 * minor, patch] tuple. In other words, this allows patch and minor updates for
 * versions 1.0.0 and above, patch updates for versions 0.X >=0.1.0, and no
 * updates for versions 0.0.X.
 * 
 * Many authors treat a 0.x version as if the x were the major "breaking-change"
 * indicator.
 * 
 * Caret ranges are ideal when an author may make breaking changes between 0.2.4
 * and 0.3.0 releases, which is a common practice. However, it presumes that
 * there will not be breaking changes between 0.2.4 and 0.2.5. It allows for
 * changes that are presumed to be additive (but non-breaking), according to
 * commonly observed practices.
 * 
 * ^1.2.3 := >=1.2.3 <2.0.0 ^0.2.3 := >=0.2.3 <0.3.0 ^0.0.3 := >=0.0.3 <0.0.4
 * ^1.2.3-beta.2 := >=1.2.3-beta.2 <2.0.0 Note that prereleases in the 1.2.3
 * version will be allowed, if they are greater than or equal to beta.2. So,
 * 1.2.3-beta.4 would be allowed, but 1.2.4-beta.2 would not, because it is a
 * prerelease of a different [major, minor, patch] tuple. ^0.0.3-beta :=
 * >=0.0.3-beta <0.0.4 Note that prereleases in the 0.0.3 version only will be
 * allowed, if they are greater than or equal to beta. So, 0.0.3-pr.2 would be
 * allowed. When parsing caret ranges, a missing patch value desugars to the
 * number 0, but will allow flexibility within that value, even if the major and
 * minor versions are both 0.
 * 
 * ^1.2.x := >=1.2.0 <2.0.0 ^0.0.x := >=0.0.0 <0.1.0 ^0.0 := >=0.0.0 <0.1.0 A
 * missing minor and patch values will desugar to zero, but also allow
 * flexibility within those values, even if the major version is zero.
 * 
 * ^1.x := >=1.0.0 <2.0.0 ^0.x := >=0.0.0 <1.0.0
 * 
 * @param the
 *          rule to apply
 * @param the
 *          instance to match against
 * 
 * @return
 */

public class NpmCaretVersion implements NpmVersionRange {

  private NpmExactVersion version;

  public NpmCaretVersion(String version) {
    this.version = NpmVersionParser.getDefaultInstance().parsePartial(version);
  }

  @Override
  public <R> R apply(NpmVersionRangeVisitor<R> visitor) {
    return visitor.visitCaretRange(this);
  }

  /**
   * calculate if the version passed in matches our version.
   */

  @Override
  public boolean satisfiedBy(NpmExactVersion version) {

    if (this.version.isPrerelease()) {

      // if our caret version is prerelease, only allow other pre-releases in
      // the same {major, minor, patch}.
      if (version.withoutPrerelease().compareTo(this.version.withoutPrerelease()) != 0) {
        return false;
      }

    } else if (version.isPrerelease()) {
      // if we are not a prerelease but the version we are testing is, then it's
      // not for us!
      return false;
    }

    NpmVersionPart pos = this.version.leftMostNonZeroPosition();

    if (pos == null || pos == NpmVersionPart.Patch) {
      return version.compareTo(this.version) == 0;
    }

    NpmExactVersion lessThan = this.version.withIncrement(this.version.leftMostNonZeroPosition());

    return version.compareTo(this.version) >= 0 && version.compareTo(lessThan) < 0;

  }

  public String toString() {
    return String.format("^%s", this.version);
  }

}