package io.zrz.jnpm;

import static org.junit.Assert.*;

import org.junit.Test;

import io.zrz.jnpm.semver.NpmVersionRange;
import io.zrz.jnpm.semver.NpmWildcardRange;

public class NpmWildcardRangeTest {

  @Test
  public void test() {

    assertTrue(NpmVersionParser.parse("0.4.X").satisfiedBy(NpmVersionParser.parseVersion("0.4")));
    assertTrue(NpmVersionParser.parse("0.4.X").satisfiedBy(NpmVersionParser.parseVersion("0.4.4")));
    assertTrue(NpmVersionParser.parse("0.4.X").satisfiedBy(NpmVersionParser.parseVersion("0.4.0")));

    assertFalse(NpmVersionParser.parse("0.4.X").satisfiedBy(NpmVersionParser.parseVersion("0.5.0")));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid0() {
    NpmWildcardRange.fromParts(-1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid1() {
    NpmWildcardRange.fromParts(-1, 4, -1);
  }

}
