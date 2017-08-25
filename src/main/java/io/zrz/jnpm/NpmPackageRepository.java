package io.zrz.jnpm;

import java.nio.file.Path;
import java.util.List;

import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;
import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.VersionRange;

/**
 * A repository which provides responses to queries about packages.
 * 
 * @author theo
 *
 */

public interface NpmPackageRepository {

  NpmPackageMeta query(String name);

  ExactVersion distTag(String name, String version);

  NpmPackageVersionMeta query(String name, ExactVersion version);

  List<ExactVersion> query(String name, VersionRange versionSpec);

  Path dist(String packageName, ExactVersion packageVersion);

}
