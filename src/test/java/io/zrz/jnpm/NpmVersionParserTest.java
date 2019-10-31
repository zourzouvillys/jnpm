package io.zrz.jnpm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.zrz.jnpm.semver.VersionParser;
import io.zrz.jnpm.semver.WildcardRange;

public class NpmVersionParserTest {

  @Test
  public void test() {
    assertEquals(WildcardRange.fromParts(), VersionParser.parseWildcard("*").get());
    assertEquals(WildcardRange.fromParts(0), VersionParser.parseWildcard("0").get());
    assertEquals(WildcardRange.fromParts(0, 1), VersionParser.parseWildcard("0.1").get());
    assertEquals(WildcardRange.fromParts(0, 1), VersionParser.parseWildcard("0.1.x").get());
  }

}
