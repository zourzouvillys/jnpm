package io.zrz.jnpm.semver;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Any class implementing this interface provides a specification that any given
 * version could match. It does not provide an "exact" version number, see
 * {@link ExactVersion} for that.
 *
 * @see https://docs.npmjs.com/files/package.json#dependencies
 *
 * @author theo
 *
 */

public interface VersionRange extends VersionSpec {

  /**
   * tests if the given exact version satisfies this spec.
   *
   * @param version
   * @return
   */

  boolean satisfiedBy(ExactVersion version);

  /**
   *
   * @param visitor
   * @return
   */

  <R> R apply(SemanticVersionVisitor<R> visitor);

  /**
   *
   * @param value
   * @return
   */

  @JsonCreator
  public static VersionRange parse(String value) {
    return VersionParser.parseRange(value);
  }

}
