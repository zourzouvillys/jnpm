package io.zrz.jnpm.semver;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(overshadowImplementation = true)
public abstract class VersionQualifier implements VersionSpec, WithVersionQualifier {

  public static final VersionQualifier EMPTY = ImmutableVersionQualifier.of(null, null);

  /**
   * The prerelease tag.
   */

  @Value.Parameter
  @Nullable
  public abstract String pre();

  /**
   * The build tag.
   */

  @Value.Parameter
  @Nullable
  public abstract String buildTag();

  public VersionQualifier withoutPrerelease() {
    return this.withPre(null);
  }

  static VersionQualifier emptyQualifier() {
    return EMPTY;
  }

  static ImmutableVersionQualifier.Builder builder() {
    return ImmutableVersionQualifier.builder();
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder();

    if (this.pre() != null) {
      sb.append('-').append(this.pre());
    }
    if (this.buildTag() != null) {
      sb.append('+').append(this.buildTag());
    }

    return sb.toString();

  }

  public final boolean isPrerelease() {
    return this.pre() != null;
  }

}
