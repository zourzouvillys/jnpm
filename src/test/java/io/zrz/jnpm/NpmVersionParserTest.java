package io.zrz.jnpm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.zrz.jnpm.semver.NpmWildcardRange;

public class NpmVersionParserTest {

  @Test
  public void test() {
    assertEquals(NpmWildcardRange.fromParts(), NpmVersionParser.getDefaultInstance().tryParseWildcard("*"));
    assertEquals(NpmWildcardRange.fromParts(0), NpmVersionParser.getDefaultInstance().tryParseWildcard("0"));
    assertEquals(NpmWildcardRange.fromParts(0, 1), NpmVersionParser.getDefaultInstance().tryParseWildcard("0.1"));
    assertEquals(NpmWildcardRange.fromParts(0, 1), NpmVersionParser.getDefaultInstance().tryParseWildcard("0.1.x"));
  }


}
