package io.zrz.jnpm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Lists;

import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.VersionPart;

public class NpmExactVersionTest {

  @Test
  public void test() {

    assertEquals("13.0.0", ExactVersion.fromString("12.4.4").withIncrement(VersionPart.Major).toString());
    assertEquals("12.5.0", ExactVersion.fromString("12.4.4").withIncrement(VersionPart.Minor).toString());
    assertEquals("12.4.5", ExactVersion.fromString("12.4.4").withIncrement(VersionPart.Patch).toString());

  }

  @Test
  public void testRelease() {
    assertEquals("12.4.5-0", ExactVersion.fromString("12.4.4").withPrerelease().toString());
    assertEquals("12.4.5-beta.0", ExactVersion.fromString("12.4.4").withPrerelease("beta").toString());
    assertEquals("12.4.5-beta.1", ExactVersion.fromString("12.4.5-beta.0").withPrerelease("beta").toString());
    assertEquals("12.4.5-beta.0", ExactVersion.fromString("12.4.5-beta0").withPrerelease("beta").toString());
    assertEquals("12.4.5-rc.0", ExactVersion.fromString("12.4.5-beta.0").withPrerelease("rc").toString());
    assertEquals("12.4.5-rc.0", ExactVersion.fromString("12.4.5-beta.1").withPrerelease("rc").toString());
    assertEquals("13.0.0-rc.0",
        ExactVersion.fromString("12.4.5-rc.1").withPrerelease(VersionPart.Major, "rc").toString());
  }

  @Test
  public void testCompareTo() {

    assertTrue(compareTo("13.0.0", "13.0.0") == 0);
    assertTrue(compareTo("13-beta.1", "13-beta.1") == 0);

    assertTrue(compareTo("12.0.0", "13.0.0") < 0);
    assertTrue(compareTo("12.1.0", "13.0.0") < 0);
    assertTrue(compareTo("12.9", "13.0.0") < 0);
    assertEquals(-1, compareTo("13-beta.1", "13-beta.2"));

    assertTrue(compareTo("13.0.1", "13.0.0") > 0);
    assertTrue(compareTo("13.1.0", "13.0.0") > 0);
    assertTrue(compareTo("14.21.3", "13.0.0") > 0);
    assertEquals(1, compareTo("14-beta.1", "13-beta.2"));

    // [{fbjs@0.8.0}, {fbjs@0.8.3}, {fbjs@0.8.1}, {fbjs@0.8.4}, {fbjs@0.8.2}]

    assertTrue(compareTo("0.8.0", "0.8.1") < 0);
    assertTrue(compareTo("0.8.1", "0.8.2") < 0);
    assertTrue(compareTo("0.8.2", "0.8.3") < 0);
    assertTrue(compareTo("0.8.3", "0.8.4") < 0);
    assertTrue(compareTo("0.8.4", "0.8.5") < 0);

    assertTrue(compareTo("0.8.2", "0.8.4") < 0);

    
    assertTrue(compareTo("1.0.0", "1") == 0);
    assertTrue(compareTo("0.9", "1") < 0);
    assertTrue(compareTo("1", "0.9") > 0);

    
    ArrayList<ExactVersion> list = Lists.newArrayList(
        ExactVersion.fromString("0.8.0"),
        ExactVersion.fromString("0.8.1"),
        ExactVersion.fromString("0.8.2"),
        ExactVersion.fromString("0.8.3"),
        ExactVersion.fromString("0.8.4"));

    Collections.sort(list);

  }

  private int compareTo(String str1, String str2) {
    return ExactVersion.fromString(str1).compareTo(ExactVersion.fromString(str2));
  }

}
