package io.zrz.jnpm;

import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;
import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.TagIdentifier;
import io.zrz.jnpm.semver.VersionRange;

public class NpmUtils {

  public static NpmPackageVersionMeta latest(NpmPackageMeta pkg) {
    final ExactVersion latest = pkg.distTags.get(TagIdentifier.LATEST);
    return pkg.versions.get(latest);
  }

  public static NpmPackageVersionMeta highest(NpmPackageMeta meta, VersionRange spec) {
    return latest(meta);
  }

}
