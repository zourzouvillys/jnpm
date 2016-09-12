package io.zrz.jnpm;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;
import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;

/**
 * a single context for transactional session.
 * 
 * this provides caching and other per operation (which may involve multiple
 * queries to the server) state.
 * 
 */

public class NpmSession {

  private final NpmPackageRepository repo;

  public NpmSession(NpmPackageRepository repo) {
    this.repo = repo;
  }

  public NpmPackageMeta meta(String name) {
    return repo.query(name);
  }

  /**
   * fetches the package info for a specific label, which can be a version, tag,
   * or range.
   * 
   * @param name
   * @param resolutionStrategy
   * @param tagspec
   * @return
   * @return
   */

  public List<NpmExactVersion> matching(String name, NpmVersionRange versionSpec) {
    Preconditions.checkNotNull(versionSpec);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(StringUtils.trimToNull(name)));
    return repo.query(name, versionSpec)
        .stream()
        .collect(Collectors.toList());
  }

  public NpmPackageVersionMeta meta(String name, NpmExactVersion version) {
    return repo.query(name, version);
  }

  public Path dist(String packageName, NpmExactVersion packageVersion) {
    return repo.dist(packageName, packageVersion);
  }

  /**
   * find the current version for a specific tag.
   * 
   * @param tag
   * @return
   */

  public NpmExactVersion distTag(String name, String tag) {
    return repo.distTag(name, tag);
  }

}
