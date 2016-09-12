package io.zrz.jnpm.semver;

import io.zrz.jnpm.NpmPackageUrlIdentifier;
import io.zrz.jnpm.NpmTagIdentifier;

public interface NpmVersionRangeVisitor<T> {

  T visitExactVersion(NpmExactVersion v);

  T visitWildcardRange(NpmWildcardRange v);

  T visitUnaryExpressionVersion(NpmUnaryVersion v);

  T visitCaretRange(NpmCaretVersion v);

  T visitCollection(NpmVersionCollection v);

  T visitTag(NpmTagIdentifier v);

  T visitNpmPackageUrlIdentifier(NpmPackageUrlIdentifier v);

}
