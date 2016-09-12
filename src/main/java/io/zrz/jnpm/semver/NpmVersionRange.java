package io.zrz.jnpm.semver;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.zrz.jnpm.NpmVersionParser;

/**
 * Any class implementing this interface provides a specification that any given
 * version could match. It does not provide an "exact" version number, see
 * {@link NpmExactVersion} for that.
 * 
 * @see https://docs.npmjs.com/files/package.json#dependencies
 * 
 * @author theo
 *
 */

public interface NpmVersionRange {

  /**
   * tests if the given exact version satisfies this spec.
   * 
   * @param version
   * @return
   */

  boolean satisfiedBy(NpmExactVersion version);

  /**
   * 
   * @param visitor
   * @return
   */

  <R> R apply(NpmVersionRangeVisitor<R> visitor);

  /**
   * 
   * @param value
   * @return
   */

  @JsonCreator
  public static NpmVersionRange parse(String value) {
    return NpmVersionParser.parse(value);
  }

}
