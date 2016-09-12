package io.zrz.jnpm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.zrz.jnpm.semver.NpmBinaryOperator;
import io.zrz.jnpm.semver.NpmCaretVersion;
import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmUnaryVersion;
import io.zrz.jnpm.semver.NpmVersionCollection;
import io.zrz.jnpm.semver.NpmVersionQualifier;
import io.zrz.jnpm.semver.NpmVersionRange;
import io.zrz.jnpm.semver.NpmWildcardRange;

public class NpmVersionParser {

  private static final NpmVersionParser INSTANCE = new NpmVersionParser();

  private static final Pattern strict = Pattern
      .compile(
          "^v?([0-9xX\\*]+)(?:\\.([0-9xX\\*]+)(?:\\.([0-9xX\\*]+))?)?(?:\\-([-0-9A-Za-z\\.]+))?(?:\\+([-0-9A-Za-z\\.]+))?$");

  private static final Pattern p = Pattern
      .compile(
          "^v?([0-9xX\\*]+)(?:\\.([0-9xX\\*]+)(?:\\.([0-9xX\\*]+))?)?(?:\\-?([-0-9A-Za-z\\.]+))?(?:\\+([-0-9A-Za-z\\.]+))?$");

  //
  public static NpmVersionParser getDefaultInstance() {
    return INSTANCE;
  }

  private static final Pattern WILDCARD_PATTERN = Pattern
      .compile(
          "^v?([0-9xX\\*]+)"
              + "(?:\\.([0-9xX\\*]+)(?:\\.([0-9xX\\*]+))?)?"
              + "(?:\\-?([-0-9A-Za-z\\.]+))?(?:\\+([-0-9A-Za-z\\.]+))?$");

  /**
   * Parses a version with a wildcard in it.
   */

  public NpmWildcardRange tryParseWildcard(String value) {

    final Matcher r = WILDCARD_PATTERN.matcher(value);

    if (!r.matches()) {
      return null;
    }

    int major = parseWildcardInt(r.group(1));
    int minor = parseWildcardInt(r.group(2));
    int patch = parseWildcardInt(r.group(3));

    return new NpmWildcardRange(major, minor, patch);

  }

  private int parseWildcardInt(String group) {
    if (group == null)
      return -1;
    switch (group) {
      case "X":
      case "x":
      case "*":
        return -1;
    }
    return Integer.parseInt(group);
  }

  /**
   * Parses an exact version number. wildcards are not allowed, and a major,
   * minor, and patch MUST all be specified.
   * 
   * @param value
   * @return
   */

  public NpmExactVersion parsePartial(String value) {

    Matcher r = p.matcher(value);

    Preconditions.checkArgument(r.matches(), value);

    return new NpmExactVersion(
        parseInt(r.group(1)),
        parseInt(r.group(2)),
        parseInt(r.group(3)),
        NpmVersionQualifier.builder().pre(r.group(4)).buildTag(r.group(5)).build());

  }

  private Integer parseInt(String group) {
    if (group == null)
      return -1;
    switch (group) {
      case "X":
      case "x":
      case "*":
        return -1;
    }
    return Integer.parseInt(group);
  }

  /**
   * Parse a range expression.
   * 
   * @param value
   * @return
   */

  public static NpmVersionRange parse(String value) {

    try {

      if (value.contains(":")) {
        return new NpmPackageUrlIdentifier(value);
      }

      Scanner s = new Scanner(value);

      // Separator only after end of part.
      s.useDelimiter("(?<=([a-zA-Z0-9\\*])) ");

      List<List<String>> values = new LinkedList<>();
      List<String> collection = new LinkedList<>();

      try {

        while (s.hasNext()) {

          String val = s.next().trim();

          if (val.startsWith("||")) {
            values.add(new ArrayList<>(collection));
            collection.clear();
            val = val.substring(2).trim();
          }

          collection.add(val);

        }

        values.add(new LinkedList<>(collection));
        collection.clear();

      } finally {
        s.close();
      }

      if (values.size() == 1) {
        // optimial (and normal) case.
        return parseGroup(values.get(0));
      }

      return new NpmVersionCollection(NpmBinaryOperator.Or,
          values.stream().map(NpmVersionParser::parseGroup).collect(Collectors.toList()));

    } catch (Exception ex) {
      throw new RuntimeException(value, ex);
    }

  }

  static NpmVersionRange parseGroup(List<String> values) {

    if (values.size() == 1) {
      // optimial (and normal) case.
      return parseRangeOrExact(values.get(0));
    }

    return new NpmVersionCollection(
        NpmBinaryOperator.And,
        values.stream().map(val -> parseRangeOrExact(val)).collect(Collectors.toList()));

  }

  public static NpmVersionRange parseRangeOrExact(String value) {

    value = StringUtils.trimToNull(value);

    if (Pattern.matches("^v?[0-9]+\\.[0-9]+\\.[0-9]+$", value)) {
      // a full exact version.
      return parseVersion(value);
    }

    if (value.startsWith(">=")) {
      return new NpmUnaryVersion(NpmVersionOperator.GreaterOrEqual, parseVersion(value.substring(2)));
    } else if (value.startsWith(">")) {
      return new NpmUnaryVersion(NpmVersionOperator.Greater, parseVersion(value.substring(1)));
    } else if (value.startsWith("<=")) {
      return new NpmUnaryVersion(NpmVersionOperator.LessOrEqual, parseVersion(value.substring(2)));
    } else if (value.startsWith("<")) {
      return new NpmUnaryVersion(NpmVersionOperator.Less, parseVersion(value.substring(1)));
    } else if (value.startsWith("~")) {
      return new NpmUnaryVersion(NpmVersionOperator.TildeRange, parseVersion(value.substring(1)));
    } else if (value.startsWith("^")) {
      return new NpmCaretVersion(value.substring(1));
    } else if (value.startsWith("=")) {
      return parseVersion(value.substring(1));
    }

    // handle a wildcard version.

    NpmWildcardRange version = NpmVersionParser.getDefaultInstance().tryParseWildcard(value);

    if (version != null) {
      // something like '1.2.x'
      return version;
    }

    // version1 - version2 Same as >=version1 <=version2.

    // range1 || range2 Passes if either range1 or range2 are satisfied.
    // git... See 'Git URLs as Dependencies' below
    // user/repo See 'GitHub URLs' below
    // tag A specific version tagged and published as tag See npm-tag
    // path/path/path See Local Paths below

    return new NpmTagIdentifier(value);

  }

  /**
   * Parses an exact semver, e.g "5.6.4-beta.1+edfrwf".
   * 
   * @param value
   * @return
   */

  public static NpmExactVersion parseVersion(String value) {
    return NpmVersionParser.getDefaultInstance().parsePartial(value.trim());
  }

}
