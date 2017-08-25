package io.zrz.jnpm;

import static org.junit.Assert.*;

import org.junit.Test;

import io.zrz.jnpm.semver.VersionParser;
import io.zrz.jnpm.semver.VersionRange;
import io.zrz.jnpm.semver.WildcardRange;

public class NpmWildcardRangeTest {

  @Test
  public void test() {

    assertTrue(VersionParser.parseRange("0.4.X").satisfiedBy(VersionParser.parseVersion("0.4")));
    assertTrue(VersionParser.parseRange("0.4.X").satisfiedBy(VersionParser.parseVersion("0.4.4")));
    assertTrue(VersionParser.parseRange("0.4.X").satisfiedBy(VersionParser.parseVersion("0.4.0")));

    assertFalse(VersionParser.parseRange("0.4.X").satisfiedBy(VersionParser.parseVersion("0.5.0")));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid0() {
    WildcardRange.fromParts(-1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid1() {
    WildcardRange.fromParts(-1, 4, -1);
  }

}
