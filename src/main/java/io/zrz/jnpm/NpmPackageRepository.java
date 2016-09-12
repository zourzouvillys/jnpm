package io.zrz.jnpm;

import java.nio.file.Path;
import java.util.List;

import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;
import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;

/**
 * A repository which provides responses to queries about packages.
 * 
 * @author theo
 *
 */

public interface NpmPackageRepository {

  NpmPackageMeta query(String name);

  NpmExactVersion distTag(String name, String version);

  NpmPackageVersionMeta query(String name, NpmExactVersion version);

  List<NpmExactVersion> query(String name, NpmVersionRange versionSpec);

  Path dist(String packageName, NpmExactVersion packageVersion);

}
