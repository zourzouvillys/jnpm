package io.zrz.jnpm;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import io.zrz.jnpm.semver.CaretVersion;
import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.VersionPart;

public class NpmCaretVersionTest {

  @Test
  public void test() {

    assertTrue(new CaretVersion("1").satisfiedBy(ExactVersion.fromString("1.2.3")));
    assertTrue(new CaretVersion("0.2").satisfiedBy(ExactVersion.fromString("0.2.3")));
    assertTrue(new CaretVersion("0.0.1").satisfiedBy(ExactVersion.fromString("0.0.1")));

    assertTrue(new CaretVersion("15").satisfiedBy(ExactVersion.fromString("15.0.0")));

    assertEquals(VersionPart.Minor, ExactVersion.fromString("0.8.1").leftMostNonZeroPosition());

    assertTrue(new CaretVersion("0.8.1").satisfiedBy(ExactVersion.fromString("0.8.4")));

    assertFalse(new CaretVersion("1.0.3").satisfiedBy(ExactVersion.fromString("3.0.0")));
    assertFalse(new CaretVersion("2").satisfiedBy(ExactVersion.fromString("1.2.3")));
    assertFalse(new CaretVersion("1.2").satisfiedBy(ExactVersion.fromString("0.2.3")));
    assertFalse(new CaretVersion("1.2").satisfiedBy(ExactVersion.fromString("0.0.3")));
    assertFalse(new CaretVersion("0.0.1").satisfiedBy(ExactVersion.fromString("0.0.3")));

    assertTrue(new CaretVersion("1.2.3-beta.2").satisfiedBy(ExactVersion.fromString("1.2.3-beta.3")));

    assertFalse(new CaretVersion("1.2.3-beta.2").satisfiedBy(ExactVersion.fromString("1.2.4-beta.2")));

  }

}
