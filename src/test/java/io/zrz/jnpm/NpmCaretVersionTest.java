package io.zrz.jnpm;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import io.zrz.jnpm.semver.NpmCaretVersion;
import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionPart;

public class NpmCaretVersionTest {

  @Test
  public void test() {

    assertTrue(new NpmCaretVersion("1").satisfiedBy(NpmExactVersion.fromString("1.2.3")));
    assertTrue(new NpmCaretVersion("0.2").satisfiedBy(NpmExactVersion.fromString("0.2.3")));
    assertTrue(new NpmCaretVersion("0.0.1").satisfiedBy(NpmExactVersion.fromString("0.0.1")));

    assertTrue(new NpmCaretVersion("15").satisfiedBy(NpmExactVersion.fromString("15.0.0")));

    assertEquals(NpmVersionPart.Minor, NpmExactVersion.fromString("0.8.1").leftMostNonZeroPosition());

    assertTrue(new NpmCaretVersion("0.8.1").satisfiedBy(NpmExactVersion.fromString("0.8.4")));

    assertFalse(new NpmCaretVersion("1.0.3").satisfiedBy(NpmExactVersion.fromString("3.0.0")));
    assertFalse(new NpmCaretVersion("2").satisfiedBy(NpmExactVersion.fromString("1.2.3")));
    assertFalse(new NpmCaretVersion("1.2").satisfiedBy(NpmExactVersion.fromString("0.2.3")));
    assertFalse(new NpmCaretVersion("1.2").satisfiedBy(NpmExactVersion.fromString("0.0.3")));
    assertFalse(new NpmCaretVersion("0.0.1").satisfiedBy(NpmExactVersion.fromString("0.0.3")));

    assertTrue(new NpmCaretVersion("1.2.3-beta.2").satisfiedBy(NpmExactVersion.fromString("1.2.3-beta.3")));

    assertFalse(new NpmCaretVersion("1.2.3-beta.2").satisfiedBy(NpmExactVersion.fromString("1.2.4-beta.2")));

  }

}
