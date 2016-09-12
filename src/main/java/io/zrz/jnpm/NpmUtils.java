package io.zrz.jnpm;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;
import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;

public class NpmUtils {

  public static NpmPackageVersionMeta latest(NpmPackageMeta pkg) {
    NpmExactVersion latest = pkg.distTags.get(NpmTagIdentifier.LATEST);
    return pkg.versions.get(latest);
  }

  public static NpmPackageVersionMeta highest(NpmPackageMeta meta, NpmVersionRange spec) {
    return latest(meta);
  }

  /**
   * filters the given available packages to return only those matching the
   * filter.
   * 
   * @param available
   * @param filter
   * 
   * @return The entries in available which match the filter.
   */

  public static Set<NpmExactVersion> matching(Set<NpmExactVersion> available, NpmVersionRange filter) {
    Objects.requireNonNull(available);
    Objects.requireNonNull(filter);
    return available.stream().filter(i -> filter.satisfiedBy(i)).collect(Collectors.toSet());
  }

  public static List<NpmExactVersion> sort(Collection<NpmExactVersion> possible) {
    List<NpmExactVersion> ordered = new LinkedList<>(possible);
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

  public static boolean allowedMatch(NpmExactVersion v1, NpmExactVersion v2) {

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
