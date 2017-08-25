package io.zrz.jnpm.semver;

import io.zrz.jnpm.NpmPackageUrlIdentifier;

public interface SemanticVersionVisitor<T> {

  T visitExactVersion(ExactVersion v);

  T visitWildcardRange(WildcardRange v);

  T visitUnaryExpressionVersion(UnaryVersion v);

  T visitCaretRange(CaretVersion v);

  T visitCollection(VersionCollection v);

  T visitTag(TagIdentifier v);

  T visitNpmPackageUrlIdentifier(NpmPackageUrlIdentifier v);

}
