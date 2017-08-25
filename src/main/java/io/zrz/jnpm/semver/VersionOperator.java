package io.zrz.jnpm.semver;

public enum VersionOperator {

  GreaterOrEqual(">="),

  Greater(">"),

  LessOrEqual("<="),

  Less("<"),

  TildeRange("~")

  //
  ;

  private String str;

  private VersionOperator(String str) {
    this.str = str;
  }

  public String toString() {
    return this.str;
  }
}
