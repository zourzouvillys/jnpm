package io.zrz.jnpm.semver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.zrz.jnpm.NpmPackageUrlIdentifier;

public class VersionParser {

  private static final Pattern strict = Pattern
      .compile(
          "^v?([0-9xX\\*]+)(?:\\.([0-9xX\\*]+)(?:\\.([0-9xX\\*]+))?)?(?:\\-([-0-9A-Za-z\\.]+))?(?:\\+([-0-9A-Za-z\\.]+))?$");

  private static final Pattern p = Pattern
      .compile(
          "^v?([0-9xX\\*]+)(?:\\.([0-9xX\\*]+)(?:\\.([0-9xX\\*]+))?)?(?:\\-?([-0-9A-Za-z\\.]+))?(?:\\+([-0-9A-Za-z\\.]+))?$");

  private static final Pattern WILDCARD_PATTERN = Pattern
      .compile(
          "^v?([0-9xX\\*]+)"
              + "(?:\\.([0-9xX\\*]+)(?:\\.([0-9xX\\*]+))?)?"
              + "(?:\\-?([-0-9A-Za-z\\.]+))?(?:\\+([-0-9A-Za-z\\.]+))?$");

  /**
   * Parses a version with a wildcard in it.
   */

  public static Optional<WildcardRange> parseWildcard(String value) {

    final Matcher r = WILDCARD_PATTERN.matcher(value);

    if (!r.matches()) {
      return Optional.empty();
    }

    final int major = parseWildcardInt(r.group(1));
    final int minor = parseWildcardInt(r.group(2));
    final int patch = parseWildcardInt(r.group(3));

    return Optional.of(new WildcardRange(major, minor, patch));

  }

  private static int parseWildcardInt(String group) {
    if (group == null) {
      return -1;
    }
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

  public static ExactVersion parseExact(String value) {

    final Matcher r = p.matcher(value);

    Preconditions.checkArgument(r.matches(), value);

    return new ExactVersion(
        VersionParser.parseInt(r.group(1)),
        VersionParser.parseInt(r.group(2)),
        VersionParser.parseInt(r.group(3)),
        VersionQualifier.builder().pre(r.group(4)).buildTag(r.group(5)).build());

  }

  private static Integer parseInt(String group) {
    if (group == null) {
      return -1;
    }
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

  public static VersionRange parseRange(String value) {

    try {

      if (value.contains(":")) {
        return new NpmPackageUrlIdentifier(value);
      }

      final Scanner s = new Scanner(value);

      // Separator only after end of part.
      s.useDelimiter("(?<=([a-zA-Z0-9\\*])) ");

      final List<List<String>> values = new LinkedList<>();
      final List<String> collection = new LinkedList<>();

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

      return new VersionCollection(BinaryOperator.Or,
          values.stream().map(VersionParser::parseGroup).collect(Collectors.toList()));

    } catch (final Exception ex) {
      throw new RuntimeException(value, ex);
    }

  }

  static VersionRange parseGroup(List<String> values) {

    if (values.size() == 1) {
      // optimial (and normal) case.
      return parseRangeOrExact(values.get(0));
    }

    return new VersionCollection(
        BinaryOperator.And,
        values.stream().map(val -> parseRangeOrExact(val)).collect(Collectors.toList()));

  }

  public static VersionRange parseRangeOrExact(String value) {

    value = StringUtils.trimToNull(value);

    if (Pattern.matches("^v?[0-9]+\\.[0-9]+\\.[0-9]+$", value)) {
      // a full exact version.
      return parseVersion(value);
    }

    if (value.startsWith(">=")) {
      return new UnaryVersion(VersionOperator.GreaterOrEqual, parseVersion(value.substring(2)));
    } else if (value.startsWith(">")) {
      return new UnaryVersion(VersionOperator.Greater, parseVersion(value.substring(1)));
    } else if (value.startsWith("<=")) {
      return new UnaryVersion(VersionOperator.LessOrEqual, parseVersion(value.substring(2)));
    } else if (value.startsWith("<")) {
      return new UnaryVersion(VersionOperator.Less, parseVersion(value.substring(1)));
    } else if (value.startsWith("~")) {
      return new UnaryVersion(VersionOperator.TildeRange, parseVersion(value.substring(1)));
    } else if (value.startsWith("^")) {
      return new CaretVersion(value.substring(1));
    } else if (value.startsWith("=")) {
      return parseVersion(value.substring(1));
    }

    // handle a wildcard version.

    final Optional<WildcardRange> version = VersionParser.parseWildcard(value);

    if (version.isPresent()) {
      // something like '1.2.x'
      return version.get();
    }

    // version1 - version2 Same as >=version1 <=version2.

    // range1 || range2 Passes if either range1 or range2 are satisfied.
    // git... See 'Git URLs as Dependencies' below
    // user/repo See 'GitHub URLs' below
    // tag A specific version tagged and published as tag See npm-tag
    // path/path/path See Local Paths below

    return new TagIdentifier(value);

  }

  /**
   * Parses an exact semver, e.g "5.6.4-beta.1+edfrwf".
   *
   * @param value
   * @return
   */

  public static ExactVersion parseVersion(String value) {
    return VersionParser.parseExact(value.trim());
  }

}
