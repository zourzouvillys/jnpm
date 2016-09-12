package io.zrz.jnpm;

public enum NpmVersionOperator {

  GreaterOrEqual(">="),

  Greater(">"),

  LessOrEqual("<="),

  Less("<"),

  TildeRange("~")

  //
  ;

  private String str;

  private NpmVersionOperator(String str) {
    this.str = str;
  }

  public String toString() {
    return this.str;
  }
}
