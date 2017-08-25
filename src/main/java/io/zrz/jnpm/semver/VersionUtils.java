package io.zrz.jnpm.semver;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class VersionUtils {

  /**
   * filters the given available packages to return only those matching the
   * filter.
   *
   * @param available
   * @param filter
   *
   * @return The entries in available which match the filter.
   */

  public static Set<ExactVersion> matching(Set<ExactVersion> available, VersionRange filter) {
    Objects.requireNonNull(available);
    Objects.requireNonNull(filter);
    return available.stream().filter(i -> filter.satisfiedBy(i)).collect(Collectors.toSet());
  }

  public static List<ExactVersion> sort(Collection<ExactVersion> possible) {
    final List<ExactVersion> ordered = new LinkedList<>(possible);
    Collections.sort(ordered);
    return ordered;
  }

  /**
   * tests prerelease allowed matching.
   *
   * @param version
   * @param other
   * @return
   */

  public static boolean allowedMatch(ExactVersion v1, ExactVersion v2) {

    if (v1.isPrerelease() && !v2.isPrerelease()) {
      return true;
    }

    if (!v1.isPrerelease() && v2.isPrerelease()) {
      return false;
    }

    if (!v1.isPrerelease() && !v2.isPrerelease()) {
      return true;
    }

    if (v1.withoutPrerelease().compareTo(v2.withoutPrerelease()) < 0) {
      return false;
    }

    return true;

  }

}
