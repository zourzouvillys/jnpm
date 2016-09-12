package io.zrz.jnpm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;

public class NpmVersionQualifierTest {

  private NpmVersionRange parse(String string) {
    return NpmVersionParser.parse(string);
  }

  @Test
  public void test0() {
    assertFalse(parse("~1.0.8").satisfiedBy(NpmExactVersion.fromString("1.6.0")));
  }

  @Test
  public void test1() {

    assertTrue(parse("~0.1.7").satisfiedBy(NpmExactVersion.fromString("0.1.43")));
    assertTrue(parse("~1.2.3").satisfiedBy(NpmExactVersion.fromString("1.2.3")));
    assertTrue(parse("~1.2.3").satisfiedBy(NpmExactVersion.fromString("1.2.4")));

    assertFalse(parse("~1.2.3").satisfiedBy(NpmExactVersion.fromString("1.3.0")));
    assertFalse(parse("~1.2.3").satisfiedBy(NpmExactVersion.fromString("1.2.2")));

  }

  @Test
  public void test2() {

    assertTrue(parse("~1.2").satisfiedBy(NpmExactVersion.fromString("1.2")));
    assertTrue(parse("~1.2").satisfiedBy(NpmExactVersion.fromString("1.2.3")));
    assertTrue(parse("~1.2").satisfiedBy(NpmExactVersion.fromString("1.2.4")));

    assertFalse(parse("~1.2").satisfiedBy(NpmExactVersion.fromString("1.1.2")));
    assertFalse(parse("~1.2").satisfiedBy(NpmExactVersion.fromString("1.3.0")));

  }

  @Test
  public void test3() {

    assertTrue(parse("~1").satisfiedBy(NpmExactVersion.fromString("1")));
    assertTrue(parse("~1").satisfiedBy(NpmExactVersion.fromString("1.0.0")));
    assertTrue(parse("~1").satisfiedBy(NpmExactVersion.fromString("1.1")));
    assertTrue(parse("~1").satisfiedBy(NpmExactVersion.fromString("1.1.0")));
    assertTrue(parse("~1").satisfiedBy(NpmExactVersion.fromString("1.0.1")));

    assertFalse(parse("~1").satisfiedBy(NpmExactVersion.fromString("0")));
    assertFalse(parse("~1").satisfiedBy(NpmExactVersion.fromString("0.2.2")));
    assertFalse(parse("~1").satisfiedBy(NpmExactVersion.fromString("2.0.0")));
    assertFalse(parse("~1").satisfiedBy(NpmExactVersion.fromString("2.2.2")));

  }

  @Test
  public void test4() {

    assertTrue(parse("~0").satisfiedBy(NpmExactVersion.fromString("0")));
    assertTrue(parse("~0").satisfiedBy(NpmExactVersion.fromString("0.1.0")));
    assertTrue(parse("~0").satisfiedBy(NpmExactVersion.fromString("0.0.1")));

    assertFalse(parse("~0").satisfiedBy(NpmExactVersion.fromString("1")));
    assertFalse(parse("~0").satisfiedBy(NpmExactVersion.fromString("1.2.2")));
    assertFalse(parse("~0").satisfiedBy(NpmExactVersion.fromString("1.0.0")));
    assertFalse(parse("~0").satisfiedBy(NpmExactVersion.fromString("1.2.2")));

  }

  @Test
  public void test5() {
    assertTrue(parse("~1.2.3-beta.2").satisfiedBy(NpmExactVersion.fromString("1.2.3-beta.4")));
    assertFalse(parse("~1.2.3-beta.2").satisfiedBy(NpmExactVersion.fromString("1.2.4-beta.2")));
  }

  @Test
  public void test6() {
    assertTrue(parse(">= 1.0.0").satisfiedBy(NpmExactVersion.fromString("1.0.0")));
    assertFalse(parse(">= 1.0.0").satisfiedBy(NpmExactVersion.fromString("0.1.0")));
  }

  @Test
  public void test7() {

    System.err.println(parse("1.2.3"));
    System.err.println(parse("v1.2.3"));
    System.err.println(parse("~1"));
    System.err.println(parse("^1.0.0"));

    System.err.println(parse("1.X"));
    System.err.println(parse("1.0.*"));

    System.err.println(parse("v1.X.X"));
    System.err.println(parse("v1.0.*"));

  }

  @Test
  public void testOr() {
    System.err.println(parse("=1 || 2"));
    System.err.println(parse("2 3 || 4"));
    System.err.println(parse("^2 <=2"));
  }

}
